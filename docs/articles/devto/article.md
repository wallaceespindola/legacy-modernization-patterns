---
title: "Strangle, don't rewrite: patterns around a COBOL core"
published: false
description: "Java 25 code for the patterns around a Strangler Fig facade on a COBOL core: an ACL with COMP-3 decoding, a classified parallel run and an outbox."
tags: java, architecture, legacy, tutorial
---

![Banner for a hands-on guide to the patterns around a strangler facade in core banking modernization](banner.png)

Say your bank runs its ledger on a COBOL core, and the mobile team wants a modern account inquiry API. Nobody wants a big-bang rewrite of the core, so someone draws a facade in front of the mainframe and says the magic words: Strangler Fig. Route one operation at a time to new code, and let the old system shrink.

That's the right starting point, and [Martin Fowler's Strangler Fig Application page](https://martinfowler.com/bliki/StranglerFigApplication.html) explains it well. The trouble starts the day after the facade goes in. The new service has to read packed decimals out of copybook records. It has to know whether a balance is posted or only memo-posted. It has to prove it computes the same interest as the old code before anyone flips a route. And once it publishes events, it has to survive the broker delivering the same event twice.

That work falls to the patterns you add around the facade. This article covers the hands-on part: the Java code for three of those patterns (an anti-corruption layer, a parallel run with mismatch classification and a transactional outbox with an idempotent consumer), plus the read side you put next to them. Everything comes from a small Java 25 module in [legacy-modernization-patterns on GitHub](https://github.com/wallaceespindola/legacy-modernization-patterns), with no Spring and no database, so you can run it on a laptop in seconds.

If you want the "why" behind not rewriting legacy systems blind, I wrote about that earlier in [Stop rewriting legacy systems, use AI to understand them first](https://dev.to/wallaceespindola/stop-rewriting-legacy-systems-use-ai-to-understand-them-first-21ba). This one stays close to the code.

## The pattern map

Here's where each pattern sits around the facade, and which package in the repo implements it:

![Pattern map in three rows: routing, where clients call a strangler facade that sends shadow and canary traffic to a parallel run; the legacy path, where the ACL fronts the COBOL core; and the modern path, where the modern service writes to an outbox table in the same transaction and a relay publishes to an event bus read by an idempotent consumer](diagrams/pattern-map.png)

The facade is the interception point. It starts as a pure pass-through (100% of traffic to legacy, no behavior change), and you only flip a route after the patterns below have done their job. Rolling back means changing a route, with no deployment involved.

The module is organized by pattern:

| Package | Pattern | Main class |
| --- | --- | --- |
| `acl` | Anti-Corruption Layer | `AccountTranslator` |
| `interest` | Branch by Abstraction, Parallel Run | `InterestCalculatorToggle`, `ParallelRun` |
| `outbox` | Transactional Outbox, Idempotent Consumer | `OutboxRelay`, `IdempotentConsumer` |
| `posting` | Safe financial writes (unknown outcome) | `SafePostingClient` |

I'll skip `posting` here. It deserves its own article, and the repo has it if you're curious.

## Step 1: the anti-corruption layer

The ACL is the boundary between the legacy world and your domain model. On a mainframe that means a few concrete jobs: EBCDIC to UTF-8, packed decimal (COMP-3) to `BigDecimal`, fixed-width copybook records to typed objects and legacy return codes to domain outcomes. The example record in the repo is 20 bytes: a 10-byte account ID, a 1-byte type flag, a 7-byte COMP-3 balance and a 2-byte return code. The ID, flag and return code are EBCDIC text (code page `IBM037`); the balance is COMP-3.

### Decoding COMP-3

Packed decimal stores two digits per byte, one per nibble, and puts the sign in the last nibble: `C` or `F` for positive, `D` for negative. So the bytes `01 23 45 6C` with scale 2 are `1234.56`. Here's the decoder from `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/acl/PackedDecimal.java`, trimmed to the main method and the sign check:

```java
public static BigDecimal decode(byte[] bytes, int scale) {
    if (bytes == null || bytes.length == 0) {
        throw new IllegalArgumentException("Empty packed decimal");
    }
    BigInteger digits = BigInteger.ZERO;
    int last = bytes.length - 1;
    for (int i = 0; i <= last; i++) {
        digits = append(digits, bytes[i] >> 4 & 0xF);
        if (i < last) {
            digits = append(digits, bytes[i] & 0xF);
        }
    }
    boolean negative = isNegative(bytes[last] & 0xF);
    return new BigDecimal(negative ? digits.negate() : digits, scale);
}

private static boolean isNegative(int signNibble) {
    return switch (signNibble) {
        case SIGN_POSITIVE_C, SIGN_POSITIVE_F -> false;
        case SIGN_NEGATIVE -> true;
        default -> throw new IllegalArgumentException("Invalid packed sign nibble: " + signNibble);
    };
}
```

Two details matter. The high nibble of the last byte is a digit, the low nibble is the sign, which is why the loop skips the low nibble only on the last byte. And anything unexpected (a digit nibble above 9, an unknown sign) throws (the digit check lives in `append`, omitted above). You don't want a corrupt record to quietly become a balance of zero. `LegacyAccountRecord.parse` catches that exception and rethrows it as a `LegacyTechnicalException`, so callers see one kind of legacy failure.

### Semantic translation

Once the bytes are decoded, you still have to decode the meaning. On many cores, the same balance field can hold a ledger (posted) balance or a memo-posted (pending) one, depending on a record type flag. If that flag leaks into your API, every consumer has to learn what `'M'` means, and you've just copied the legacy model into the new system.

Here's the translator from `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/acl/AccountTranslator.java`, trimmed to the two methods:

```java
public AccountLookup translate(byte[] raw) {
    LegacyAccountRecord record = LegacyAccountRecord.parse(raw);
    return switch (record.returnCode()) {
        case OK -> new Found(new AccountBalance(record.accountId(), record.balance(), typeOf(record.typeFlag())));
        case NOT_FOUND -> new NotFound(record.accountId());
        case BLOCKED -> new Blocked(record.accountId());
        default -> throw new LegacyTechnicalException("Legacy return code " + record.returnCode());
    };
}

private static BalanceType typeOf(char flag) {
    return switch (flag) {
        case LEDGER_FLAG -> BalanceType.POSTED;
        case MEMO_POSTED_FLAG -> BalanceType.PENDING;
        default -> throw new LegacyTechnicalException("Unknown record type flag " + flag);
    };
}
```

Return codes `"00"`, `"04"` and `"08"` become `Found`, `NotFound` and `Blocked`, three records of a sealed `AccountLookup` interface, so the compiler makes callers handle each case. Flags `'L'` and `'M'` become `BalanceType.POSTED` and `BalanceType.PENDING`. Nothing named `typeFlag` or `returnCode` leaves the `acl` package.

A few rules I'd hold the ACL to:

- **One ACL per bounded context:** an account ACL, not a "mainframe ACL" that every team adds to.
- **Translation only:** no business rules. Once it starts deciding things, it's a new monolith with extra latency.
- **Retire it with the legacy capability:** an ACL with no legacy system behind it is dead weight.

## Step 2: the read side, pending and posted

The first capability most teams move is account inquiry, because reads are low risk and high volume. The usual shape is change data capture (CDC) from the legacy store into a read model, with the ledger staying the system of record. That's CQRS without event sourcing: the mainframe ledger is already the write model.

![Read-side flow in two rows: the facade sends balance reads to the account inquiry service while CDC lag is within its SLO and falls back to a legacy call through the ACL when it isn't; below, the COBOL ledger memo-posts during the day and hard-posts at end of day, and CDC feeds a translation step into a read model with pending and posted balances](diagrams/read-side-flow.png)

The repo doesn't ship a CDC pipeline (it would need a database and a broker), but the ACL above is exactly what the translation step needs. Raw CDC records are legacy-shaped. If you write them straight into the read model, the new model inherits the old schema, flags included. So run them through the same kind of translation, and store `POSTED` and `PENDING` as separate, explicit values.

Many cores memo-post during the day and hard-post in the end-of-day batch, and CDC sees both. If the read model has one "balance" column, customers will watch their balance jump around at night. With pending and posted modeled explicitly, the UI can show both and nothing surprises anyone.

Two more things to put in place before you route reads to the new service:

- **A lag SLO with a fallback route:** if CDC falls behind, the facade sends balance reads back to legacy through the ACL. That keeps the step reversible.
- **Per-key ordering and dedup:** partition by account ID, and drop duplicates by source log position.

## Step 3: the parallel run

Before the facade sends real customers to new logic, you want evidence that the new logic agrees with the old one. A parallel run computes both, compares them, records the result and serves the legacy answer. The repo does this for monthly interest, behind a Branch by Abstraction toggle with three modes: `LEGACY`, `SHADOW` and `MODERN`.

The two calculators disagree on purpose. The legacy one uses 30/360, truncates the intermediate result to four decimals, then rounds half-up (COBOL `ROUNDED`). The modern one uses actual days over 365 and rounds half-even. Those are exactly the differences that show up in real parallel runs: rounding mode, day-count convention and intermediate truncation.

Here's the core of `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/interest/ParallelRun.java`, trimmed to the two methods that matter:

```java
@Override
public BigDecimal monthlyInterest(BigDecimal balance, BigDecimal annualRate, YearMonth month) {
    BigDecimal legacyResult = legacy.monthlyInterest(balance, annualRate, month);
    BigDecimal modernResult = modern.monthlyInterest(balance, annualRate, month);
    BigDecimal difference = modernResult.subtract(legacyResult).abs();
    comparisons.add(new Comparison(balance, annualRate, month, legacyResult, modernResult, difference,
            classify(difference, month)));
    return legacyResult;
}

private MismatchCategory classify(BigDecimal difference, YearMonth month) {
    if (difference.signum() == 0) {
        return MismatchCategory.MATCH;
    }
    if (difference.compareTo(ONE_CENT) <= 0) {
        return MismatchCategory.ROUNDING;
    }
    if (difference.compareTo(tolerance) <= 0) {
        return MismatchCategory.WITHIN_TOLERANCE;
    }
    boolean oddMonthLength = month.lengthOfMonth() != DAYS_IN_LEGACY_MONTH;
    return oddMonthLength ? MismatchCategory.DAY_COUNT : MismatchCategory.UNEXPLAINED;
}
```

The last line of `monthlyInterest` is the important one: it always returns `legacyResult`. So shadow mode has no customer-visible effect. (In a real system you'd also stub notification and payment adapters on the shadow path, and enforce that in configuration.)

The classification is what makes the output usable. A raw list of mismatches turns into a spreadsheet nobody reads. Buckets turn it into a triage conversation: rounding noise, within tolerance, a known day-count difference or something a human has to look at. With a balance of 1000 at 5% in January, the tests show legacy at `4.17` and modern at `4.25`. The difference of `0.08` is above the one-cent tolerance, and January has 31 days, so it lands in `DAY_COUNT`, not `UNEXPLAINED`. `ParallelRunReport` then counts comparisons per category.

The classifier is a heuristic, and the Javadoc in the repo says so. In a real run you'd grow the categories as you explain differences (business date vs system date, time zones), and only `UNEXPLAINED` should block a cutover. Once the counts look right, the rollout goes shadow, then canary (a small segment gets the modern result while legacy is still computed and compared), then full cutover with legacy kept warm for a rollback window.

## Step 4: the outbox and the idempotent consumer

When the new service starts owning business events (a notification, a fraud signal), it needs to change its own state and publish an event without the two drifting apart. Writing to the database and then calling the broker is a dual write, and a crash between the two loses the event or publishes one for a change that never committed.

The transactional outbox fixes that: the state change and an outbox row commit in one local transaction, and a relay publishes outbox rows later. The repo models the transaction with `InMemoryUnitOfWork`, which commits ledger rows and outbox rows together or not at all. The relay, from `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/outbox/OutboxRelay.java`, trimmed to its one method:

```java
public int relayOnce() {
    int sent = 0;
    for (OutboxEvent event : store.unpublished()) {
        try {
            publisher.publish(event);
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Publish failed for {0}, will retry: {1}", event.eventId(), e.getMessage());
            break;
        }
        store.markPublished(event.eventId());
        sent++;
    }
    return sent;
}
```

It stops at the first failure to keep the order, and it marks an event as published only after the broker accepted it. That second rule means a crash after `publish` and before `markPublished` sends the event again on the next run. Delivery is at-least-once, and there's no way around that on the producer side.

So the consumer has to make duplicates harmless. Here's `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/outbox/IdempotentConsumer.java`, trimmed to the method:

```java
public synchronized boolean accept(OutboxEvent event) {
    if (inbox.contains(event.eventId())) {
        return false;
    }
    handler.accept(event);
    inbox.add(event.eventId());
    return true;
}
```

The inbox is a set of processed event IDs. A redelivered event returns `false` and the handler never runs twice. In production, the inbox is a table keyed by event ID, and you insert into it in the same transaction as the consumer's own state change. Otherwise you've moved the dual-write problem from the producer to the consumer.

On the mainframe side, the same idea exists: write the event to a Db2 outbox table, or put it on MQ, inside the same CICS unit of work as the business update. For the relay you can poll, as the example does, or tail the outbox table with CDC, which avoids polling load and keeps commit order.

## Things that will trip you up

**A leaky ACL.** If a legacy field name, a return code or a record shape shows up in a new API or event, the ACL has a hole. Review contracts for it the same way you review for secrets.

**Rounding that isn't a bug.** COBOL `ROUNDED` is half-up by default, while Java's `BigDecimal` needs an explicit `RoundingMode`. Plenty of "mismatches" in a parallel run come from this one line, so classify them as a group instead of chasing them one by one.

**Shadow traffic with side effects.** A shadow path that sends a real notification or books a real entry is worse than no parallel run. Stub every adapter with a side effect, and enforce it in configuration so it doesn't depend on someone catching it in review.

**A consumer that is "mostly" idempotent.** An inbox that is updated after the handler commits, in a separate transaction, will process a duplicate eventually. Write the inbox row in the same transaction as the handler's changes.

**The perpetual strangler.** Facades, ACLs, CDC pipelines and parallel-run harnesses are transitional architecture. Each one needs a removal date, or you end up running and paying for both stacks indefinitely. [Patterns of Legacy Displacement](https://martinfowler.com/articles/patterns-legacy-displacement/) by Ian Cartwright, Rob Horn and James Lewis calls this out well.

## Where it doesn't pay off

This toolkit has a cost: you build, run and later delete everything in it. If the legacy capability is small enough to replace in one well-tested release, coexistence may cost more than the cutover. If it's stable, cheap to run and not blocking anyone, retaining it behind an API is a good outcome. And if there's no interception point at all (no API, queue or data boundary), the facade has nowhere to go, so you start with event interception or encapsulation first. There's no adoption data here. Choose the patterns per capability, and add each one only where it solves a problem you actually have.

## Recap

- The strangler facade decides where traffic goes. It starts as a pass-through and every flip is a routing change you can undo.
- The ACL decodes COMP-3 and EBCDIC, but its real job is semantics: return codes become domain outcomes, flags become `POSTED` and `PENDING`.
- The read side keeps pending and posted apart, has a lag SLO and falls back to legacy when it's behind.
- The parallel run serves the legacy answer, compares and classifies, so you triage categories instead of rows.
- The outbox gives you at-least-once publishing without dual writes, and the idempotent consumer makes the "at least" part safe.

## Try it yourself

You need Java 25 and Maven. Clone [the repo](https://github.com/wallaceespindola/legacy-modernization-patterns) and run the build:

```bash
cd examples/account-modernization && mvn verify
```

All 36 tests should pass in a few seconds. Good places to start reading are `PackedDecimalTest` (the byte-level examples), `ParallelRunTest` (the classification cases, including the `4.17` vs `4.25` one) and `OutboxTest` (a broker that fails, then recovers and gets the same event again). Then pass a tolerance of `0.10` to `ParallelRun` and rerun the January case: the `0.08` `DAY_COUNT` gap becomes `WITHIN_TOLERANCE`. The pattern catalog behind the code, including the phases and exit criteria, is in [docs/legacy-modernization-patterns-context.md](https://github.com/wallaceespindola/legacy-modernization-patterns/blob/main/docs/legacy-modernization-patterns-context.md).

Which of these patterns did your team find hardest to get right around a legacy core: the ACL, the parallel run or the outbox? I'd like to hear in the comments.

*I wrote this article with AI assistance, then reviewed, fact-checked and edited it myself.*

**Examples repository:** <https://github.com/wallaceespindola/legacy-modernization-patterns>

Need more tech insights?
Check out my GitHub, LinkedIn, and Speaker Deck.
Happy coding!

Wallace Espindola, Solution Architect

- GitHub: <https://github.com/wallaceespindola/>
- LinkedIn: <https://www.linkedin.com/in/wallaceespindola/>
- Speaker Deck: <https://speakerdeck.com/wallacese>

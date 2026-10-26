---
title: "Prove it before you route it"
platform: Substack
newsletter: Code & Coffee
author: Wallace Espindola
description: "Run the modernized service next to legacy, compare every result and explain every difference before you flip the route."
tags: [legacy-modernization, strangler-fig, parallel-run, core-banking, java]
---

**Suggested subject line:** Can we flip it on Monday?

*Preview text: The habit that makes flipping a route boring: run both, compare, explain every difference.*

---

![Banner for Prove it before you route it](banner.png)

# Prove it before you route it

Hey folks,

Here is a moment I suspect plenty of modernization teams reach sooner or later. Picture a team that has spent months on a modern Java service that calculates monthly interest, replacing logic the COBOL core has run for years. The tests pass, the code review is done and the facade in front of the mainframe is ready to send traffic to the new service with one configuration change. Someone asks the obvious question: "Can we flip it on Monday?"

Passing tests tell you the new code does what its authors think it should. They don't tell you it does what the old system does, and in banking the old system is the definition of correct until someone signs off otherwise. A one-cent difference on one account is noise. The same cent on every account, every month, is a reconciliation break, a customer complaint and an audit finding.

I put the patterns around this in a repo, with a small Java module you can run: <https://github.com/wallaceespindola/legacy-modernization-patterns>. This issue is the coffee-length version: one habit, what it looks like in practice, where it doesn't pay off and what to try on Monday. The habit itself is short: prove it before you route it.

## What a parallel run looks like

The idea is old. For a while, both implementations get the same input. The legacy system keeps serving the customer, the new one computes its answer on the side and something compares the two and records the difference. Nobody outside the team sees the new result yet. [Patterns of Legacy Displacement](https://martinfowler.com/articles/patterns-legacy-displacement/) by Ian Cartwright, Rob Horn and James Lewis lists the parallel run among the ways to deliver a replacement safely, and it sits naturally next to the [Strangler Fig Application](https://martinfowler.com/bliki/StranglerFigApplication.html): the strangler decides where traffic goes, the parallel run tells you whether it's safe to send it there.

In the example module, the parallel run is just another implementation of the same interface, so the caller doesn't know it's in a comparison. Here's the method, trimmed from `examples/account-modernization/src/main/java/com/wallaceespindola/modernization/interest/ParallelRun.java` (the fields and the classifier are left out):

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
```

The last line returns the legacy result, always, so a bug in the new calculator can't reach a customer. And every comparison is stored with a category, which is what turns a pile of differences into a conversation the team can have. A toggle (`InterestCalculatorToggle` in the same package) switches between legacy, shadow and modern at runtime, so moving from one stage to the next is a configuration change.

## The usual false mismatches

The first time you run a comparison like this, you will see a lot of red. A good share of it usually traces back to the new service being "more correct" in a way nobody asked for. Three causes come up again and again.

**Rounding mode:** COBOL's `ROUNDED` phrase rounds a tie away from zero by default, which is what Java calls `HALF_UP`. Java's `BigDecimal` makes you pick a [`RoundingMode`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/RoundingMode.html) explicitly, and a developer who has read about banker's rounding may well pick `HALF_EVEN`. Both are reasonable. Only one matches the system of record. Intermediate truncation adds to it: the legacy calculator in the repo truncates to four decimals before it rounds, the modern one doesn't.

**Day-count convention:** the legacy calculator uses 30/360 (every month has 30 days, the year has 360). The modern one uses ACT/365 (the real number of days in the month over 365). In the repo's test, a balance of 1,000 at 5% for January 2026 gives 4.17 on the legacy side and 4.25 on the modern side. Eight cents apart, and both are "right" under their own convention.

**Business date vs system date:** the mainframe books by business date, which moves when the end-of-day batch says so, not when the clock passes midnight. Say the cut-off is 23:00: a transaction at 23:50 can belong to tomorrow's business date. A new service that uses the system clock will disagree on exactly those transactions, and usually around month-end, when it hurts most. Time zones and holiday calendars belong to the same family.

Naming these causes is what makes triage possible. In the module, `ParallelRun` sorts each difference into `MATCH`, `ROUNDING`, `WITHIN_TOLERANCE`, `DAY_COUNT` or `UNEXPLAINED`. The classifier is a heuristic and says so in its Javadoc. One of the tests runs April (30 days long, so the heuristic can't blame the month length) and the gap (4.17 against 4.11) lands in `UNEXPLAINED`, even though the real cause is still the 360 versus 365 denominator. That's fine, because a human looks at that bucket, finds the cause and either fixes the new code or writes the difference down as accepted. The rule I'd hold to is that nothing moves forward while `UNEXPLAINED` has rows nobody has looked at.

## Side effects off, by configuration

A shadow path that sends an email, posts a ledger entry or calls a payment network has become a second production system that nobody is watching, and in banking that means duplicate customer-facing or financial side effects.

So the shadow side writes to sandboxed stores and runs with its notification and payment adapters stubbed, and that rule needs enforcing. "Everybody knows not to wire the real adapter in shadow mode" is a convention, and conventions fail on the day someone new copies a configuration file. Enforce it by configuration instead, so that in shadow mode the real adapters can't be wired in at all (the example module doesn't show this part; its toggle only switches calculators). It's dull work, and it's the work that keeps a parallel run from turning into an incident.

## Shadow, canary, cutover, then a rollback window

Proving equivalence takes a few stages, each with its own exit criterion and each reversible by a routing change.

![Four rollout stages drawn as a loop: shadow, where the customer gets the legacy result and the new one is only compared; canary, where a small segment gets the new result while legacy is still computed and compared; cutover, where everyone gets the new result and legacy is kept warm; and the rollback window, which closes after sign-off. A note below says an unexplained mismatch or a problem in canary or cutover flips the route back.](diagrams/route-flip-stages.png)

**Shadow:** the customer gets the legacy answer, the new one is computed and compared, then thrown away. You leave this stage when every mismatch is explained and the comparison has covered at least one month-end, because that's when batch timing, accruals and business dates misbehave.

**Canary:** a small segment (one channel, or a group of internal or low-risk accounts) gets the new answer. Legacy is still computed and compared for that segment. If an unexplained mismatch shows up, the route goes back to legacy for that segment while you look. You leave canary when your SLOs hold and the team has rehearsed the rollback at least once.

**Cutover:** everyone gets the new answer. Legacy stays warm, still able to take traffic, for a rollback window you agree on in advance. Rollback here has to be a routing change at the facade, because nobody wants to redeploy at 2 a.m. The window ends when the agreed period is over and reconciliation is clean.

**Window closes:** only after reconciliation is clean and the business and risk owners sign off do you remove the legacy path. "Later, when we have time" is how teams end up paying for both stacks for years.

This only holds while the legacy system still owns the writes. If the new service has already taken write ownership, rolling back also means getting data back into legacy, which is a much bigger conversation. That's one more reason to do the parallel run while legacy is still the system of record.

## Where it doesn't pay off

A parallel run costs something. You run two implementations, you pay for the compute twice and on a mainframe the extra calls show up in MIPS or MSU consumption, which is often what the bill is based on. Someone has to build the comparison, tune the tolerances, keep the list of accepted differences and work through the triage queue, week after week. The comparison ends up as a small product of its own.

It earns that cost on calculations and decisions with financial or customer impact: interest, fees, balances, risk rules, eligibility checks. It's overkill for a capability whose output nobody depends on, for a rewrite of a screen that only displays what another system computed or for a system small enough to replace in one well-tested release. And if you are moving to a vendor package, you can't usually run its internals side by side the same way, though you can still reconcile its outputs against legacy.

A parallel run can also turn into a place where the team hides. If the mismatch list never shrinks because nobody owns the triage, the shadow stage never ends. Give it an owner and a review rhythm from the first day.

## Try this on Monday

You don't need a full rollout plan to start. Pick one calculation and prove one thing:

1. Pick one read-only calculation your modernization will replace, one with money attached (monthly interest, a fee, a displayed balance).
2. Put both implementations behind the same interface, with the legacy result always returned, the way `ParallelRun` does in the repo.
3. Write down your known differences before you run anything: rounding mode, day-count convention, which date you use. You'll be surprised how many the team can't answer for the legacy side.
4. Run it over a sample of real historical inputs that includes a month-end, and sort every difference into a category.
5. Look at the `UNEXPLAINED` bucket with someone who knows the legacy code. Each row is either a bug, a hidden requirement or an accepted difference that needs a name and a sign-off.

If you'd like to see it working first, clone the repo and run `mvn verify` in `examples/account-modernization/`. `ParallelRunTest` walks through the January and April cases above and the toggle moving from legacy to shadow to modern.

If you missed it, my earlier issue on [the future of legacy modernization](https://wallaceespindola.substack.com/p/the-future-of-legacy-modernization) covers the step before this one: understanding the legacy system before you replace any of it.

## Wrapping up

Tests tell you the new code works. A parallel run tells you it behaves like the system your customers and your auditors already trust, and the differences it finds are usually rounding, day counts and dates rather than dramatic bugs. Run both, keep the shadow side harmless by configuration, explain every mismatch and move through shadow, canary and cutover with a route you can flip back. Then flipping the route on Monday is a boring decision, which is the kind you want.

My question for you: what was the strangest mismatch you ever found between a legacy system and its replacement? Hit reply or drop a comment. I read every one.

**Examples repository:** <https://github.com/wallaceespindola/legacy-modernization-patterns>

Need more tech insights?
Check out my GitHub, LinkedIn, and Speaker Deck.
Happy coding!

Wallace Espindola, Solution Architect
GitHub: <https://github.com/wallaceespindola/>
LinkedIn: <https://www.linkedin.com/in/wallaceespindola/>
Speaker Deck: <https://speakerdeck.com/wallacese>

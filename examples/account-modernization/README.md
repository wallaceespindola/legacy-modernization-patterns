# Account modernization examples

Plain Java 25 code behind the legacy modernization article series: four small, dependency-free examples of
patterns used around a COBOL core banking system. No Spring, no database, so the build runs offline and fast.
Terms and behavior follow `docs/legacy-modernization-patterns-context.md`.

| Package | Pattern | Main class |
| --- | --- | --- |
| `acl` | Anti-Corruption Layer | `AccountTranslator` |
| `interest` | Branch by Abstraction, Parallel Run | `InterestCalculatorToggle`, `ParallelRun` |
| `outbox` | Transactional Outbox, Idempotent Consumer | `OutboxRelay`, `IdempotentConsumer` |
| `posting` | Safe financial writes (unknown outcome) | `SafePostingClient` |

```bash
mvn verify
```

Author: Wallace Espindola, Solution Architect. [GitHub](https://github.com/wallaceespindola/),
[LinkedIn](https://www.linkedin.com/in/wallaceespindola/). Licensed under Apache 2.0.

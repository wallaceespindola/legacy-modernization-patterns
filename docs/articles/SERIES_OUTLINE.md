# Series outline

One theme across seven platforms: Strangler Fig is the migration framework for core banking modernization, and the complementary patterns (ACL, DDD, CDC, CQRS, Outbox, Parallel Run, reconciliation, safe posting) are what make the coexistence period safe. Source of truth: [`docs/legacy-modernization-patterns-context.md`](../legacy-modernization-patterns-context.md) and the Java module in [`examples/account-modernization/`](../../examples/account-modernization/).

Each platform has its own angle and title. No paragraph is shared between platforms; code snippets are identical to the repo.

| Platform | Angle | Working title | Snippets | Diagrams |
| --- | --- | --- | --- | --- |
| Dev.to | Hands-on: the patterns you add around a strangler, with the Java module | Strangle, don't rewrite: a pattern toolkit for core banking modernization | `PackedDecimal`, `AccountTranslator`, `ParallelRun`, `IdempotentConsumer` | pattern map, read-side flow |
| DZone | Governance of the coexistence period: phases, gates, data ownership, reconciliation, metrics | Legacy modernization: governing the coexistence period | `SafePostingClient` (short) | phase gates, data ownership stages |
| Medium | Opinion essay: the strangler gets the credit, coexistence is where the work is | The Strangler Fig gets the credit. Coexistence does the work | none or one short | where the effort goes over time |
| LinkedIn | Leadership: what a lead decides and funds; one capability at a time | Modernize one capability at a time. Fund the coexistence, too | none | phases (no tables in text) |
| Substack | Code & Coffee habit: prove it before you route it | Prove it before you route it | `ParallelRun` (short) | shadow, canary, cutover |
| InfoQ | Trend and pattern analysis for architects: coexistence as an architecture | Coexistence Is the Architecture: Patterns for Incremental Core Banking Modernization | none | own originals: pattern fit map, ownership transfer |
| JavaPro | Code walkthrough: legacy behavior to tested Java | From COBOL behavior to tested Java: an anti-corruption layer, a parallel run and safe posting | full listings from all four packages | own originals: module components, unknown-outcome sequence |

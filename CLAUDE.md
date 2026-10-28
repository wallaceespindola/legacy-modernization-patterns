# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A docs-first project about Strangler Fig and its complementary patterns for core banking modernization. The source of truth is `docs/legacy-modernization-patterns-context.md`; keep `README.md`, the articles and the slides consistent with it. A small Java module, `examples/account-modernization/`, implements the patterns the articles quote (ACL, Branch by Abstraction with Parallel Run, Transactional Outbox with Idempotent Consumer, safe posting with unknown outcome).

## Layout

- `docs/legacy-modernization-patterns-context.md`: pattern catalog and context.
- `examples/account-modernization/`: Java 25, Maven, JUnit 5, no Spring, no database.
- `docs/articles/<platform>/`: `article.md`, `banner.png` (InfoQ and DZone also `banner.jpg`), `diagrams/` with sources next to PNGs. `SERIES_OUTLINE.md`, `BANNER_DESIGN.md`, `PLATFORM_GUIDELINES.md` sit in `docs/articles/`.
- `docs/presentations/`: Marp `slides.md`, exported PPTX/PDF, `GOOGLE_SLIDES.md`, `diagrams/`.

## Commands

```bash
make diagrams   # render every .puml (plantuml) and .mmd (mermaid-cli) to a PNG next to its source
make lint       # markdownlint-cli2, config in .markdownlint.json
make links      # offline relative-link check (needs lychee)
make slides     # Marp export of docs/presentations/slides.md to PDF and PPTX
make examples   # mvn verify in the Java module
make format     # Prettier, one line per paragraph
```

CI (`.github/workflows/docs.yml`) runs markdownlint, the offline link check and the Java build.

## Conventions

- Code changes go in the module first; articles copy snippets from it verbatim (trimmed is fine, changed is not).
- Diagrams: keep the `.puml` / `.mmd` source next to its rendered PNG and re-render after editing.
- Prose: sentence-case headings (InfoQ title excepted), no em dashes, no emojis, no fabricated stats or anecdotes. Never name the author's employer or internal tools.
- Regulatory and vendor facts (DORA, z/OS Connect, CDC tooling) drift; verify against the official source before stating them.
- Author info: Wallace Espindola, Solution Architect, <wallace.espindola@gmail.com>, <https://www.linkedin.com/in/wallaceespindola/>, <https://github.com/wallaceespindola/>

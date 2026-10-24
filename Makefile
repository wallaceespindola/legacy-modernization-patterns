# Docs-only project: context, architecture notes and diagrams.

.PHONY: help lint format links check

help: ## Show targets
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  %-10s %s\n", $$1, $$2}'

format: ## Format Markdown with Prettier (one line per paragraph)
	npx -y prettier@3.3.3 --write --prose-wrap never --print-width 120 "**/*.md"

lint: ## Lint Markdown
	npx -y markdownlint-cli2 "**/*.md" "#node_modules"

links: ## Check relative links between Markdown files (offline)
	lychee --offline --no-progress "**/*.md"

check: lint links ## Run all checks

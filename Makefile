# Docs-first project: pattern context, articles, slides, diagrams and a Java example module.
PUML := $(shell find docs -name '*.puml')
MMD  := $(shell find docs -name '*.mmd')
DECK := docs/presentations/slides.md
NAME := legacy-modernization-patterns
# Extra mermaid-cli flags, e.g. MMDC_FLAGS="-p puppeteer.json" to point at a local Chrome
MMDC_FLAGS ?=

.PHONY: help format diagrams lint links slides examples check clean

help: ## Show targets
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  %-10s %s\n", $$1, $$2}'

format: ## Format Markdown with Prettier (one line per paragraph)
	npx -y prettier@3.3.3 --write --prose-wrap never --print-width 120 "**/*.md"

diagrams: ## Render all PlantUML and Mermaid sources to PNG next to their source
	@for f in $(PUML); do plantuml -tpng "$$f"; done
	@for f in $(MMD); do npx -y @mermaid-js/mermaid-cli $(MMDC_FLAGS) -i "$$f" -o "$${f%.mmd}.png" -b white -s 2; done

lint: ## Lint Markdown
	npx -y markdownlint-cli2 "**/*.md" "#node_modules" "#**/target"

links: ## Check relative links between Markdown files (offline)
	lychee --offline --no-progress --exclude-path examples/account-modernization/target "**/*.md"

slides: ## Export the Marp deck to PDF (Speaker Deck) and PPTX
	npx -y @marp-team/marp-cli $(DECK) --pdf --allow-local-files -o docs/presentations/$(NAME).pdf
	npx -y @marp-team/marp-cli $(DECK) --pptx --allow-local-files -o docs/presentations/$(NAME)-marp.pptx

examples: ## Build and test the Java example module
	cd examples/account-modernization && mvn -q verify

check: lint links examples ## Run all checks

clean: ## Remove generated HTML exports and build output
	rm -f docs/presentations/*.html
	rm -rf examples/account-modernization/target

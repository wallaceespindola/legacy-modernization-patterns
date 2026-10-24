# Banner design: Aurora ribbons

The series banners share one visual language so they read as a family on every platform.

## Philosophy

Two ribbons of light cross on a dark field: the old system and the new one, running side by side and meeting at one bright point. That crossing is the idea of the series. Modernization is not a jump from one system to the other, it is a long period where both carry load and have to agree, and the banners show that coexistence rather than a before and after.

The left third stays calm and dark for the words. The ribbons, glow and bokeh sit to the right, so feed crops towards the centre keep the crossing visible. Text is short (at most three words), light and never on top of the brightest point.

## How the banners vary

Same generator (`article-image-generator`, `generate_banner.py`, style `aurora`, topic `legacy-modernization-patterns`), own palette, seed and words per platform, each at the size in [PLATFORM_GUIDELINES.md](PLATFORM_GUIDELINES.md).

| Platform | Words | Palette | Seed | Size | Files |
| --- | --- | --- | --- | --- | --- |
| Dev.to | Patterns, not rewrites | lagoon | 3 | 1000x420 | `devto/banner.png` |
| DZone | Govern the coexistence | oceanic | 5 | 1200x628 | `dzone/banner.png`, `dzone/banner.jpg` |
| Medium | Beyond the fig | sunset | 7 | 1200x627 | `medium/banner.png` |
| LinkedIn | One capability first | borealis | 11 | 1920x1080 | `linkedin/banner.png` |
| Substack | One slice, safely | solar | 13 | 1200x630 | `substack/banner.png` |
| InfoQ | Coexistence by design | nebula | 17 | 1024x768 | `infoq/banner.png`, `infoq/banner.jpg` |
| JavaPro | Proven in Java | electric | 19 | 1200x630 | `javapro/banner.png` |

The README banner (`docs/images/banner.png`, "Strangle, don't replace") is about the project, not an article, and uses the `network` style.

To regenerate one:

```bash
uv run --with pillow python ~/git/ai-agents/skills/article-image-generator/scripts/generate_banner.py \
  --topic legacy-modernization-patterns --platform devto --palette lagoon --seed 3 \
  --text "Patterns, not rewrites" --out docs/articles/devto/banner.png
```

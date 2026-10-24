# Getting this deck into Google Slides

The deck is `legacy-modernization-patterns.pptx` (23 slides, 16:9, with speaker notes). It is built from the Marp source `slides.md` with python-pptx, so every text box, table and code block stays editable.

## Step 0: Get the file

- From a clone: `docs/presentations/legacy-modernization-patterns.pptx`.
- From GitHub without cloning: open [the file on GitHub](https://github.com/wallaceespindola/legacy-modernization-patterns/blob/main/docs/presentations/legacy-modernization-patterns.pptx) and click **Download raw file**.

## Option 1: Open it as a new Google Slides presentation

1. Go to [Google Slides](https://slides.google.com).
2. Click **Blank presentation**, then **File > Open** (or press Ctrl+O / Cmd+O).
3. Choose the **Upload** tab and drop `legacy-modernization-patterns.pptx` in.
4. When it opens, use **File > Save as Google Slides** so you edit a native copy, not the PPTX.

## Option 2: Add the slides to a deck you already have

1. Open your existing presentation in Google Slides.
2. Go to **File > Import slides**.
3. Choose the **Upload** tab and select `legacy-modernization-patterns.pptx`.
4. Pick **All** (or only the slides you want). Leave **Keep original theme** checked so the dark title slides and colors stay as designed.
5. Click **Import slides**. They're added after the slide you have selected.

## Option 3: Through Google Drive

1. Upload `legacy-modernization-patterns.pptx` to [Google Drive](https://drive.google.com) (**New > File upload**).
2. Right-click the file and choose **Open with > Google Slides**.
3. Use **File > Save as Google Slides** to convert it.

To have every PPTX convert automatically on upload, turn on **Drive settings > Convert uploads to Google Docs editor format**.

## After importing

- Speaker notes come along: **View > Show speaker notes**.
- The deck uses only Arial and Courier New, so fonts don't get substituted.
- Diagrams are PNG images, so they look the same as in PowerPoint.
- Check the table slides (5 and 22) and the code slides (8 and 15) once; row heights and code wrapping can shift slightly.
- To present: **Slideshow** (top right), or **Presenter view** from its dropdown to see your notes.

## Keynote

Open `legacy-modernization-patterns.pptx` in Keynote (**File > Open**). The 23 slides and the speaker notes come through (tested with Keynote on macOS).

## Speaker Deck

Speaker Deck takes PDF only. Sign in at [speakerdeck.com](https://speakerdeck.com), click **New Deck**, upload `legacy-modernization-patterns.pdf`, add the title "Strangler Fig Is Not Enough" and a description, then publish. The PDF is exported from the Marp deck (`slides.md`) and has the same 23 slides.

## Marp alternative (HTML / PDF)

`docs/presentations/slides.md` is a [Marp](https://marp.app) deck with speaker notes as HTML comments. Export it without PowerPoint:

```bash
# PDF (the Speaker Deck file) and Marp's own PPTX
make slides

# HTML
npx @marp-team/marp-cli docs/presentations/slides.md --allow-local-files -o docs/presentations/slides.html
```

`--allow-local-files` lets Marp embed the diagram PNGs. If Marp cannot find a browser, point it at Chrome with `CHROME_PATH`.

## Diagrams

Sources live in `docs/presentations/diagrams/`:

| Diagram | Source | Render |
| --- | --- | --- |
| How the patterns fit together | `pattern-map.mmd` | `mmdc -i pattern-map.mmd -o pattern-map.png -b white -s 2` |
| Data ownership stages | `ownership-stages.mmd` | `mmdc -i ownership-stages.mmd -o ownership-stages.png -b white -s 2` |
| Shadow to decommission | `rollout-stages.mmd` | `mmdc -i rollout-stages.mmd -o rollout-stages.png -b white -s 2` |
| Unknown outcome on a timed-out posting | `unknown-outcome.puml` | `plantuml -tpng unknown-outcome.puml` |

---

Author: Wallace Espindola, Solution Architect, <wallace.espindola@gmail.com>
LinkedIn: <https://www.linkedin.com/in/wallaceespindola/> | GitHub: <https://github.com/wallaceespindola/>
Repo: <https://github.com/wallaceespindola/legacy-modernization-patterns>

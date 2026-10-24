# Platform guidelines

Rules checked on 2026-10-18 against official help pages and submission guidelines where they could be reached. "Unverified" means no official source confirmed the value; the value shown is the working default from third-party guides or the platform skills. Several official pages (DZone, Medium) returned HTTP 403 to automated fetches, so their values come from search-indexed copies of the official pages.

## Dev.to

| Rule | Value | Source |
|------|-------|--------|
| Title length | No limit stated; skill target 50 to 70 chars (unverified) | [Editor guide](https://dev.to/p/editor_guide) |
| Description | `description` front matter used for Twitter and Open Graph cards; no length limit stated (unverified, keep about 150 to 160 chars) | [Editor guide](https://dev.to/p/editor_guide) |
| Tags | Max 4, comma-separated, lowercase alphanumeric | [Editor guide](https://dev.to/p/editor_guide) |
| Word count | No official limit; skill target 1,000 to 3,000 (unverified) | none |
| Cover image | 1000 x 420 (about 2.38:1) recommended | [Editor guide](https://dev.to/p/editor_guide) |
| Cover safe area | Social previews can crop to about 800 x 418; keep key content centred (community report, unverified) | [DEV thread](https://dev.to/smitterhane/what-size-should-a-cover-image-of-a-post-be-2n9b) |
| Image file size | Not stated (GIFs limited to 200 megapixels per frame) | [Editor guide](https://dev.to/p/editor_guide) |
| Text in images | No rule | none |
| Markdown | Standard Markdown, inline HTML, Liquid embeds, KaTeX; tables render (GFM); Mermaid not documented, use PNG | [Editor guide](https://dev.to/p/editor_guide) |
| AI disclosure | AI-assisted posts must be disclosed (tag `#abotwrotethis` or a statement anywhere, for example at the end); author must fact-check and stand behind it | [AI-assisted articles guidelines](https://dev.to/guidelines-for-ai-assisted-articles-on-dev) |
| Canonical | `canonical_url` front matter supported | [Editor guide](https://dev.to/p/editor_guide) |

## DZone

| Rule | Value | Source |
|------|-------|--------|
| Title length | Not stated (unverified; skill uses 60 to 80 chars) | none |
| Summary / TL;DR | Not stated (unverified) | none |
| Tags | Not stated (unverified) | none |
| Word count | Minimum 800 words; may still be rejected for lack of depth | [Submission guidelines](https://dzone.com/pages/submission-guidelines) |
| Cover image size | Not stated (unverified; 1200 x 628 kept) | none |
| Image file size | Compress each image to 100 to 200 KB, JPEG/JPG recommended; 5 MB cumulative per article | [Submission guidelines](https://dzone.com/pages/submission-guidelines) |
| Text in images | No rule found | none |
| Markdown | Rich-text editor; tables supported; Mermaid not supported, use images (unverified) | [Editor walkthrough](https://dzone.com/articles/a-walk-through-of-the-dzone-article-editor) |
| AI content | Content must be original and created by a human; articles partially or fully created with AI tools are not allowed and are rejected, account may be suspended | [Submission guidelines](https://dzone.com/pages/submission-guidelines) |
| Exclusivity / canonical | Original content required; syndication rules not confirmed (unverified) | [Submission guidelines](https://dzone.com/pages/submission-guidelines) |
| Review time | About 30 business days | [Submission guidelines](https://dzone.com/pages/submission-guidelines) |

## Medium

| Rule | Value | Source |
|------|-------|--------|
| Title length | No official limit; previews truncate around 100 chars (third-party, unverified) | none |
| Subtitle | Custom display title and subtitle set in Story preview; no official limit (unverified; keep under about 140 chars) | [Custom titles and subtitles](https://help.medium.com/hc/en-us/articles/214895188-Customize-title) |
| Tags (topics) | Up to 5 | [Using topics](https://help.medium.com/hc/en-us/articles/214741038-Using-topics) |
| Word count | No official limit (unverified) | none |
| Featured image | Wide horizontal rectangle, first image in the story; no official pixel size (unverified; 1200 x 627 kept) | [Post previews](https://help.medium.com/hc/en-us/articles/228036227-Trouble-controlling-post-previews-on-feeds) |
| Image file size | Up to 25 MB, JPG, JPEG, GIF, PNG | [Using images](https://help.medium.com/hc/en-us/articles/215679797-Using-images) |
| Text in images | No rule | none |
| Markdown | No tables, no Mermaid; use images or code blocks | [Using the story editor](https://help.medium.com/hc/en-us/articles/215194537-Using-the-story-editor) |
| AI disclosure | AI-assisted writing must be disclosed (news coverage says within the first two paragraphs); undisclosed AI writing gets network-only distribution; fully AI-generated writing cannot be paywalled; AI images must be captioned as AI-generated | [AI content policy](https://help.medium.com/hc/en-us/articles/22576852947223) |
| Canonical | Import tool sets canonical link (unverified) | none |

## LinkedIn articles

| Rule | Value | Source |
|------|-------|--------|
| Title length | Not stated officially; third-party guides say 150 chars max (unverified) | [Unilink](https://www.unilink.us/blog/linkedin-character-limits-2026) |
| Description | No meta field in the editor (unverified) | none |
| Tags | Articles have no tags; hashtags go in the share post (unverified) | none |
| Word count | No official limit; skill target 800 to 1,500 (unverified) | none |
| Cover image | 1920 x 1080 recommended, cropped to 16:9 | [Add images and rich media](https://www.linkedin.com/help/linkedin/answer/a521719), [Cover images in articles](https://www.linkedin.com/help/linkedin/answer/a518855) |
| Image formats | Cover: JPEG, PNG, WEBP (no GIF); inline: GIF, JPEG, PNG, WEBP | [Add images and rich media](https://www.linkedin.com/help/linkedin/answer/a521719) |
| Image file size | Not stated on the fetched page (search snippet reported 5 MB, unverified) | [Add images and rich media](https://www.linkedin.com/help/linkedin/answer/a521719) |
| Markdown | Rich-text editor; no tables, no Mermaid; use images | none |
| AI disclosure | No article-specific rule found (unverified) | none |
| Canonical | Not supported (unverified) | none |

## Substack

| Rule | Value | Source |
|------|-------|--------|
| Title / subtitle | No official limit found (unverified) | none |
| Tags | Post tags exist; no limit found (unverified) | none |
| Word count | No limit (unverified) | none |
| Social preview image | At least 1200 x 630; Substack also states 14:10 for previews (the two conflict, so keep content inside a central 14:10 area) | [Optimal image dimensions](https://support.substack.com/hc/en-us/articles/4408381685268-What-are-the-optimal-image-dimensions-for-my-Substack-publication) |
| Where it is set | Post settings, social preview, "Upload new" | [Edit social preview](https://support.substack.com/hc/en-us/articles/360039016992-How-do-I-edit-what-my-post-looks-like-on-social-media) |
| Image file size | Not stated officially (third-party: 10 MB, unverified) | none |
| Markdown | Rich-text editor; no native tables or Mermaid; use images (unverified) | none |
| AI disclosure | No rule found (unverified) | none |
| Canonical | Not supported (unverified) | none |

## JavaPro

| Rule | Value | Source |
|------|-------|--------|
| Length | At least 15,000 to 25,000 characters recommended | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |
| Title, tags, summary | Not specified | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |
| Featured image | Not specified (unverified; 1200 x 630 kept); author photo JPG or PNG | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |
| Text in images | Not specified | none |
| AI disclosure | Not mentioned | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |
| Exclusivity | Not required, but content must be original and not copied or republished from other sites; teaser articles linking out are not featured | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |
| Other | No product, service or event promotion; author bio and qualifications required; must be ready to publish | [Become a JAVAPRO author](https://javapro.io/become-a-javapro-author/) |

## InfoQ

| Rule | Value | Source |
|------|-------|--------|
| Length | 1,500 to 4,000 words, average 2,000 to 3,000 | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Title | Proposed title required; no length rule | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Key takeaways | Exactly 5, complete self-standing sentences, not a pitch | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Images | Own work, permissive licence or written permission; JPEG (other formats converted); vector diagrams exported as images; no size rule; editors control final art | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Markdown | Plain text, Word, Pages, Markdown or Google Docs accepted; tables become HTML tables; avoid complex formatting; Mermaid not mentioned, export diagrams | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| AI disclosure | Must disclose which AI tools were used and how; no articles or substantial sections written entirely by AI; substantial drafting help gets a standard notice on publication | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Exclusivity | Not freely available elsewhere; 4-week exclusive rights; afterwards republish with link and "originally published on InfoQ" note | [InfoQ guidelines](https://www.infoq.com/guidelines/) |
| Process | Google Form submission; one active proposal per author, max one per quarter | [InfoQ guidelines](https://www.infoq.com/guidelines/) |

## Speaker Deck

| Rule | Value | Source |
|------|-------|--------|
| Format | PDF only | [File types](https://help.speakerdeck.com/help/what-file-types-do-you-support) |
| File size | Max 150 MB (250 MB on PRO) | [Deck size](https://help.speakerdeck.com/help/how-large-of-a-deck-can-i-upload) |
| Aspect ratio | Not specified; 16:9 is the common default (unverified) | none |
| Quality | Export at high resolution to avoid blurry slides | [Speaker Deck blog](https://blog.speakerdeck.com/publishing-tips-for-storyboard-artists/) |

## Differences from the skills

| Skill | Skill says | Verified | Action |
|-------|-----------|----------|--------|
| image-generator-blog | LinkedIn 1200 x 627 | LinkedIn help: 1920 x 1080, 16:9 | Update skill |
| image-generator-blog, linkedin-pulse-formatter | LinkedIn featured image 1200 x 627 | 1920 x 1080 | Update both |
| image-generator-blog, substack-newsletter | Substack 1200 x 600 | At least 1200 x 630, 14:10 preview crop | Update both |
| image-generator-blog | Max 3 MB, 500 KB to 2 MB typical | DZone wants 100 to 200 KB per image, 5 MB per article | Add DZone note |
| image-generator-blog | Disclose AI-generated images "in article" | Medium requires AI images to be captioned as AI-generated | Make explicit for Medium |
| dzone-article | Minimum 1,000 words | Minimum 800 words | Fix number |
| dzone-article | Banner 500 KB to 2 MB | 100 to 200 KB per image, JPEG recommended | Fix number |
| dzone-article | Canonical tag pointing to DZone, exclusive first publication preferred | Not confirmed on the guidelines page (unverified) | Mark unverified |
| devto-formatter | Headline 50 to 70 chars | No official limit | Mark as guidance |
| devto-formatter | Mentions avoiding AI over-use | Official rule: disclose AI assistance (`#abotwrotethis` or a statement) | Add disclosure rule |
| medium-optimizer | Subtitle 125 to 150 chars, meta description 150 to 160 | No official limits; previews truncate | Mark as guidance |
| medium-optimizer | No AI rule | Disclose AI assistance; undisclosed gets network-only distribution | Add rule |
| linkedin-pulse-formatter | 5 to 7 tags on the article | Articles have no tags; hashtags belong in the share post (unverified) | Clarify |
| javapro-magazine | 3,000 to 6,000 words, minimum 2,500 | At least 15,000 to 25,000 characters (roughly 2,500 to 4,000 words) | Align with characters |
| javapro-magazine | Banner 1200 x 630 | No size published | Keep as proposal |
| javapro-magazine | No exclusivity note | No exclusivity required, but no republished content and no teasers | Add rule |
| infoq-article | Featured image 1024 x 768, 500 KB to 2 MB | No size rule; JPEG; editors control art | Mark size as proposal, note JPEG |
| infoq-article | 1,500 to 4,000 words, AI disclosure, 4-week exclusivity | Matches | None |

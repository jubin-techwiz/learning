# Tools

Checks for the learning notes. Run them after writing or changing any note, before committing.

```bash
./tools/check-notes.sh                                             # links and structure only
KENMORA_WEB=<path to kenmora>/web ./tools/check-notes.sh           # plus Kenmora render and Mermaid
```

| Script | Checks | Needs |
|---|---|---|
| `check-links.py` | Every relative link in every Markdown file of the repository resolves (notes, templates, tools, labs and each lab's README, progress, root files); notes link only inside `notes/`; each lab README links only inside its own lab folder, and `labs/README.md` only inside `labs/` | Python 3 |
| `check-structure.py` | Code fences closed and tagged; no lab paths or commands in notes; front matter complete (title, tags list, category matching the area folder, valid level and `updated` date); a Prerequisites line that links to a note (or says "none") and a Next topic line that links to a different note (or says the topic is planned or the series is complete); the "Step N" label matches the file path; required sections, numbered without gaps, ending with Answers; references to existing notes are links; every note is linked from `notes/README.md` and `notes/roadmap.md` | Python 3 |
| `check-java-examples.py` | Every Java block whose first line is `// File: Name.java (runnable, Java N)` is compiled and run once with the installed JDK (`java Name.java`), in its own folder; the JDK must be at least the version `N` named in the marker (Java 21 for these notes); it must exit 0, and its output must match the **Expected output** block after it exactly (or, for an **Example output** block, only exit 0 is checked) | A JDK of the stated version (`java` on the PATH); skipped with a message when none is found |
| `render-check.ts` | Every note renders in Kenmora's own pipeline without warnings | Kenmora's `web` folder with `node_modules` (`npx vite-node`) |
| `mermaid-check.mjs` | Every Mermaid diagram parses with Kenmora's Mermaid version | Kenmora's `web` folder with `node_modules` |
| `check-notes.sh` | Runs all of the above; exits non-zero if any fails | As above |

Notes still in the earlier format (with an "Industry check" heading) are listed as "old format" by the structure check instead of failing.

**Scope and exclusions of the link check:** all `*.md` files except those under `.git`, `node_modules`, `target`, `build`, `dist`, `.idea` and `.vscode`. It ignores external URLs, in-page anchors, links inside fenced code and inline code, and angle-bracket placeholders such as `(<relative link>)` in templates; it checks both `[text](target)` links and `[label]: target` definitions.

**Known limits:** link checking verifies that the target file exists, not that a `#heading` anchor exists; the structure check does not judge whether the right note is named as a prerequisite or as next, only that the line is present and points somewhere valid.

**What they do not do:** apart from the Java check, which really runs the marked examples, they check structure, metadata and links only. A passing run says nothing about whether the technical content is accurate or whether a note teaches well; that needs review against official documentation and a reader.

These scripts only read files. They don't change notes, and they don't contact any network service.

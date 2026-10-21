# Tools

Checks for the learning notes. Run them after writing or changing any note, before committing.

```bash
./tools/check-notes.sh                                             # links and structure only
KENMORA_WEB=<path to kenmora>/web ./tools/check-notes.sh           # plus Kenmora render and Mermaid
```

| Script | Checks | Needs |
|---|---|---|
| `check-links.py` | Every relative link resolves; notes link only inside `notes/` | Python 3 |
| `check-structure.py` | Code fences closed and tagged; no lab paths or commands in notes; required sections, numbered without gaps, ending with Answers; references to existing notes are links | Python 3 |
| `render-check.ts` | Every note renders in Kenmora's own pipeline without warnings | Kenmora's `web` folder with `node_modules` (`npx vite-node`) |
| `mermaid-check.mjs` | Every Mermaid diagram parses with Kenmora's Mermaid version | Kenmora's `web` folder with `node_modules` |
| `check-notes.sh` | Runs all of the above; exits non-zero if any fails | As above |

Notes still in the earlier format (with an "Industry check" heading) are listed as "old format" by the structure check instead of failing.

These scripts only read files. They don't change notes, and they don't contact any network service.

#!/usr/bin/env bash
# Runs every documentation check for the learning notes.
#   ./tools/check-notes.sh
# The links and structure checks need only Python 3. The render and Mermaid checks use
# Kenmora's own renderer; set KENMORA_WEB to Kenmora's web folder to include them.
# The Java check runs the notes' runnable Java examples and needs a JDK (`java` on the PATH).
set -u
cd "$(dirname "$0")/.."
REPO="$(pwd)"
status=0
skipped=""

python3 tools/check-links.py || status=1
python3 tools/check-structure.py || status=1

if command -v java >/dev/null 2>&1; then
  python3 tools/check-java-examples.py || status=1
else
  skipped="${skipped} Skipped: Java examples (no \`java\` on the PATH)."
  echo "java examples: SKIPPED (no java command found)"
fi

if [ -n "${KENMORA_WEB:-}" ] && [ -d "$KENMORA_WEB/node_modules" ]; then
  (cd "$KENMORA_WEB" && LEARNING_NOTES="$REPO/notes" npx vite-node "$REPO/tools/render-check.ts") || status=1
  node tools/mermaid-check.mjs || status=1
else
  skipped="${skipped} Skipped: Kenmora render and Mermaid (set KENMORA_WEB to Kenmora's web folder)."
  echo "render and mermaid: SKIPPED (set KENMORA_WEB to Kenmora's web folder to run them)"
fi

if [ $status -eq 0 ]; then
  echo "All executed checks passed.${skipped}"
else
  echo "Some checks failed.${skipped}"
fi
echo "Note: links, structure, render and Mermaid are static checks; the Java check runs the marked examples and compares their output. None of them can establish the accuracy of the prose or the quality of the teaching."
exit $status

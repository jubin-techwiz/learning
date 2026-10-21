#!/usr/bin/env bash
# Runs every documentation check for the learning notes.
#   ./tools/check-notes.sh
# The links and structure checks need only Python 3. The render and Mermaid checks use
# Kenmora's own renderer; set KENMORA_WEB to Kenmora's web folder to include them.
set -u
cd "$(dirname "$0")/.."
REPO="$(pwd)"
status=0

python3 tools/check-links.py || status=1
python3 tools/check-structure.py || status=1

if [ -n "${KENMORA_WEB:-}" ] && [ -d "$KENMORA_WEB/node_modules" ]; then
  (cd "$KENMORA_WEB" && LEARNING_NOTES="$REPO/notes" npx vite-node "$REPO/tools/render-check.ts") || status=1
  node tools/mermaid-check.mjs || status=1
else
  echo "render and mermaid: skipped (set KENMORA_WEB to Kenmora's web folder to run them)"
fi

[ $status -eq 0 ] && echo "All checks passed." || echo "Some checks failed."
exit $status

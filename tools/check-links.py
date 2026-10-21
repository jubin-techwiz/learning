#!/usr/bin/env python3
"""Check relative Markdown links in every Markdown file of the learning repository.

Scope: all *.md files, except build and tool folders (.git, node_modules, target, build,
dist, .idea, .vscode). That includes notes/, templates/, tools/, labs/ (including each lab's
own README), progress/ and the root files.

A link fails if:
  - its target file or folder does not exist;
  - a file in notes/ links outside notes/ (Kenmora imports only that folder);
  - a lab README links outside its own lab folder (labs/README.md: outside labs/), because
    labs must be self-contained (see labs/README.md, "Conventions every lab follows").

Ignored, to avoid false positives: external URLs (http, https, mailto), in-page anchors,
links inside fenced code blocks and inline code, and angle-bracket placeholders such as
(<relative link>) in templates. Inline links [text](target) and reference definitions
[label]: target are both checked.

Not checked: whether a #heading anchor exists in the target file.

Usage: python3 tools/check-links.py
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

SKIP_DIRS = {'.git', 'node_modules', 'target', 'build', 'dist', '.idea', '.vscode'}

files = []
for folder, dirs, names in os.walk('.'):
    dirs[:] = [d for d in dirs if d not in SKIP_DIRS]
    files += [os.path.normpath(os.path.join(folder, n)) for n in names if n.endswith('.md')]

INLINE = re.compile(r'\]\(([^)\s]+)(?:\s+"[^"]*")?\)')
REFERENCE = re.compile(r'^\s*\[[^\]]+\]:\s*(\S+)', re.M)


def boundary(f):
    """The folder a file may link into: notes/ for notes, the lab folder for labs, else anywhere."""
    parts = f.split(os.sep)
    if parts[0] == 'notes':
        return 'notes'
    if parts[0] == 'labs':
        return os.sep.join(parts[:3]) if len(parts) > 3 else 'labs'   # labs/<area>/<lab>/..., or labs/README.md
    return None


checked = problems = 0
for f in sorted(files):
    text = open(f, encoding='utf-8').read()
    text = re.sub(r'```.*?```', '', text, flags=re.S)   # fenced code
    text = re.sub(r'`[^`\n]*`', '', text)               # inline code
    limit = boundary(f)
    for target in INLINE.findall(text) + REFERENCE.findall(text):
        if target.startswith(('http://', 'https://', 'mailto:', '#')) or '<' in target:
            continue
        checked += 1
        path = os.path.normpath(os.path.join(os.path.dirname(f), target.split('#')[0]))
        if os.path.isabs(target) or path.startswith('..'):
            problems += 1
            print(f'BROKEN   {f}: {target} (absolute or outside the repository)')
        elif not os.path.exists(path):
            problems += 1
            print(f'BROKEN   {f}: {target}')
        elif limit and path != limit and not path.startswith(limit + os.sep):
            problems += 1
            print(f'OUTSIDE  {f}: {target} (links from here must stay inside {limit}/)')

print(f'links: {len(files)} files, {checked} links checked, {problems} problems')
sys.exit(1 if problems else 0)

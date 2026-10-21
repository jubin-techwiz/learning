#!/usr/bin/env python3
"""Check relative Markdown links in the learning repository.

Fails if a link points to a missing file, or if a note in notes/ links outside notes/
(Kenmora imports only that folder). Links inside code blocks, inline code and
angle-bracket placeholders (templates) are ignored.

Usage: python3 tools/check-links.py
"""
import glob
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

files = (glob.glob('notes/**/*.md', recursive=True)
         + glob.glob('templates/*.md')
         + [f for f in ('README.md', 'LEARNING-STANDARD.md', 'labs/README.md') if os.path.exists(f)])

checked = problems = 0
for f in sorted(files):
    text = open(f, encoding='utf-8').read()
    text = re.sub(r'```.*?```', '', text, flags=re.S)   # fenced code
    text = re.sub(r'`[^`\n]*`', '', text)               # inline code
    for m in re.finditer(r'\]\(([^)\s]+)\)', text):
        target = m.group(1)
        if target.startswith(('http://', 'https://', 'mailto:', '#')) or '<' in target:
            continue
        checked += 1
        path = os.path.normpath(os.path.join(os.path.dirname(f), target.split('#')[0]))
        if not os.path.exists(path):
            problems += 1
            print(f'BROKEN   {f}: {target}')
        elif f.startswith('notes/') and not path.startswith('notes'):
            problems += 1
            print(f'OUTSIDE  {f}: {target} (notes must link inside notes/)')

print(f'links: {len(files)} files, {checked} links checked, {problems} problems')
sys.exit(1 if problems else 0)

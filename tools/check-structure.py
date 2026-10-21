#!/usr/bin/env python3
"""Check the structure of learning notes against LEARNING-STANDARD.md.

For every note in notes/:
  - code fences are closed and carry a language tag;
  - notes never mention lab paths or run commands (notes are independent of labs);
  - topic notes in the current format have the required sections, numbered without gaps,
    and end with an Answers section;
  - references to existing notes such as "(2.2.11)" are links, not bare numbers
    (notes that are only planned may stay plain text).

Notes still in the earlier format (with an "Industry check" heading) are reported as
"old format" instead of failing, so a migration can happen step by step.

Usage: python3 tools/check-structure.py
"""
import glob
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

REQUIRED = ['The concept in simple English', 'Industry terms', 'In a real system (ShopFlow)',
            'Check your understanding', 'Revision', 'Verification and sources']
LAB_REFERENCE = re.compile(r'labs/|resilience-demo|circuit-breaker-demo|\bmvn |demo[-\w]*\.sh')
BARE_STEP = re.compile(r'(?<![\[\d.\w/-])\((\d+\.\d+(?:\.\d+)?)\)')

problems = 0
old_format = []


def step_of(path):
    """notes/02-messaging/02-kafka/05-x.md -> '2.2.5'; numbers 90 and above are not steps."""
    parts = [int(p[:2]) for p in path.split('/')[1:] if p[:2].isdigit()]
    return '.'.join(map(str, parts)) if parts and parts[-1] < 90 else None


EXISTING_STEPS = {s for s in (step_of(f) for f in glob.glob('notes/**/*.md', recursive=True)) if s}


def report(f, msg):
    global problems
    problems += 1
    print(f'{f}: {msg}')


for f in sorted(glob.glob('notes/**/*.md', recursive=True)):
    text = open(f, encoding='utf-8').read()
    lines = text.split('\n')
    name = os.path.basename(f)

    inside = False
    for i, line in enumerate(lines, 1):
        if line.startswith('```'):
            if not inside:
                inside = True
                if not line[3:].strip():
                    report(f, f'line {i}: code block without a language tag')
            else:
                if line.strip() != '```':
                    report(f, f'line {i}: unexpected text on a closing fence')
                inside = False
        elif not inside:
            if LAB_REFERENCE.search(line):
                report(f, f'line {i}: mentions a lab path or command')
    if inside:
        report(f, 'unclosed code block')

    if name in ('README.md', 'roadmap.md') or name[:2] in ('98', '99'):
        continue                                  # index, roadmap, glossary, cheat sheet: free form
    headings = re.findall(r'^## (.*)$', text, re.M)
    if any('Industry check' in h for h in headings):
        old_format.append(f)
        continue

    numbered = [h for h in headings if re.match(r'\d+\. ', h)]
    numbers = [int(h.split('.')[0]) for h in numbered]
    if numbers != list(range(1, len(numbers) + 1)):
        report(f, f'sections are not numbered 1..n without gaps: {numbers}')
    titles = [h.split('. ', 1)[1] for h in numbered]
    for req in REQUIRED:
        if not any(t.startswith(req) for t in titles):
            report(f, f'missing section "{req}"')
    if not headings or headings[-1] != 'Answers':
        report(f, 'the last section must be "Answers"')

    for i, line in enumerate(lines, 1):
        if line.startswith(('**Messaging', '**Resilience', '**Prerequisites', '**Next topic', '#')):
            continue
        for m in BARE_STEP.finditer(line):
            if m.group(1) not in EXISTING_STEPS:                 # planned notes stay plain text
                continue
            if not line[:m.start()].rstrip().endswith(')'):     # "[note](x.md) (2.2.3)" is fine
                report(f, f'line {i}: bare step reference ({m.group(1)}); make it a link')

for f in old_format:
    print(f'old format (not checked for structure): {f}')
print(f'structure: {problems} problems, {len(old_format)} notes in the old format')
sys.exit(1 if problems else 0)

#!/usr/bin/env python3
"""Check the structure of learning notes against LEARNING-STANDARD.md.

For every note in notes/:
  - code fences are closed and carry a language tag;
  - notes never mention lab paths or run commands (notes are independent of labs);
  - topic notes in the current format have the required sections, numbered without gaps,
    and end with an Answers section;
  - references to existing notes such as "(2.2.11)" are links, not bare numbers
    (notes that are only planned may stay plain text);
  - front matter has title, a non-empty tags list, a category matching the area folder,
    a valid level and a valid `updated` date;
  - topic notes have a Prerequisites line that links to a note (or says "none") and a
    Next topic line that links to a note other than itself (or says the topic is planned
    or the series is complete). Link targets are verified by check-links.py;
  - the "Step N" label in the position line matches the file's path;
  - every note is linked from notes/README.md and notes/roadmap.md.

Notes still in the earlier format (with an "Industry check" heading) are reported as
"old format" instead of failing, so a migration can happen step by step.

Usage: python3 tools/check-structure.py
"""
import datetime
import glob
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

REQUIRED = ['What it is and why it matters', 'Industry terms', 'In a real system (ShopFlow)', 'How it works',
            'Check your understanding', 'Revision', 'Verification and sources']
LAB_REFERENCE = re.compile(r'labs/|resilience-demo|circuit-breaker-demo|\bmvn |demo[-\w]*\.sh')
BARE_STEP = re.compile(r'(?<![\[\d.\w/-])\((?:see |and )?(\d+\.\d+(?:\.\d+)?)\)')   # "(2.2.5)" and "(see 2.2.5)"

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

    if name in ('README.md', 'roadmap.md'):
        continue

    # front matter
    fm = re.match(r'---\n(.*?)\n---\n', text, re.S)
    if not fm:
        report(f, 'missing front matter')
    else:
        keys = dict(re.findall(r'^(\w+):\s*(.*)$', fm.group(1), re.M))
        for k in ('title', 'tags', 'category', 'level', 'updated'):
            if k not in keys:
                report(f, f'front matter is missing "{k}"')
        if 'tags' in keys and not re.fullmatch(r'\[\s*[^\]\s][^\]]*\]', keys['tags'].strip()):
            report(f, 'front matter tags must be a non-empty list, for example [kafka, messaging]')
        area = re.match(r'notes/\d+-([^/]+)/', f)
        if area and 'category' in keys and keys['category'].strip('"\'') != area.group(1):
            report(f, f'front matter category "{keys["category"]}" does not match the area folder "{area.group(1)}"')
        if keys.get('level') not in (None, 'fundamentals', 'intermediate', 'advanced'):
            report(f, f'front matter level "{keys["level"]}" is not fundamentals, intermediate or advanced')
        if 'updated' in keys:
            try:
                d = datetime.date.fromisoformat(keys['updated'].strip('"\''))
                if d > datetime.date.today():
                    report(f, f'updated date {d} is in the future')
            except ValueError:
                report(f, f'updated "{keys["updated"]}" is not a YYYY-MM-DD date')

    # the position line must carry the step that the path implies
    expected = step_of(f)
    if expected:
        m = re.search(r'^\*\*[^\n]*?Step (\d+(?:\.\d+)*)', text, re.M)
        if not m:
            report(f, 'no "Step N" position line')
        elif m.group(1) != expected:
            report(f, f'position line says Step {m.group(1)} but the path implies {expected}')

    if name[:2] in ('98', '99'):
        continue                                  # glossary, cheat sheet: free form
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

    if step_of(f):                                     # topic notes and overviews, not glossary or cheat sheet
        link = re.compile(r'\]\(([^)\s]+\.md)(?:#[^)]*)?\)')
        pre = [l for l in lines if l.startswith('**Prerequisites:**')]
        if not pre:
            report(f, 'missing "**Prerequisites:**" line (write "none" if there are none)')
        elif not link.search(pre[0]) and not re.search(r'\bnone\b', pre[0], re.I):
            report(f, 'the Prerequisites line must link to a note or say "none"')
        nxt = [l for l in lines if l.startswith('**Next topic:**')]
        if not nxt:
            report(f, 'missing "**Next topic:**" line in the Revision section')
        else:
            targets = [os.path.normpath(os.path.join(os.path.dirname(f), t)) for t in link.findall(nxt[0])]
            if targets and all(t == os.path.normpath(f) for t in targets):
                report(f, 'the Next topic line links to the note itself')
            elif not targets and not re.search(r'\bplanned\b|series is complete|completes? (the|this)', nxt[0], re.I):
                report(f, 'the Next topic line must link to a note (or say it is planned or the series is complete)')

    for i, line in enumerate(lines, 1):
        if line.startswith(('**Messaging', '**Resilience', '**Prerequisites', '**Next topic', '#')):
            continue
        for m in BARE_STEP.finditer(line):
            if m.group(1) not in EXISTING_STEPS:                 # planned notes stay plain text
                continue
            if not line[:m.start()].rstrip().endswith(')'):     # "[note](x.md) (2.2.3)" is fine
                report(f, f'line {i}: bare step reference ({m.group(1)}); make it a link')

# every note must be reachable from the index and the roadmap
for index in ('notes/README.md', 'notes/roadmap.md'):
    content = open(index, encoding='utf-8').read()
    linked = {os.path.normpath(os.path.join(os.path.dirname(index), t))
              for t in re.findall(r'\]\(([^)\s#]+\.md)\)', content)}
    for f in sorted(glob.glob('notes/**/*.md', recursive=True)):
        if f not in (index, 'notes/README.md', 'notes/roadmap.md') and f not in linked:
            report(index, f'does not link to {f}')

for f in old_format:
    print(f'old format (not checked for structure): {f}')
print(f'structure: {problems} problems, {len(old_format)} notes in the old format')
sys.exit(1 if problems else 0)

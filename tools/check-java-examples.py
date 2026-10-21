#!/usr/bin/env python3
"""Compile and run the runnable Java examples in the notes, and compare their output.

A runnable example is a ```java block whose first line is

    // File: OrderTotals.java (runnable, Java 21)

The block must be a complete program (imports, a public class named like the file, a main
method). The text between the end of the block and the next fenced block must contain either

    **Expected output**   -> the next ```text block must match the program's output exactly
                             (trailing spaces ignored), or
    **Example output**    -> the output varies between runs; the program must still compile
                             and exit with status 0, but the output is not compared.

Expected output must contain only behaviour the specification guarantees; implementation
details and timing-dependent output belong in an **Example output** block (LEARNING-STANDARD.md,
section 8).

Each example is run in its own temporary folder with the JDK's single-file source launcher
(`java OrderTotals.java`), so it must not depend on any other file. Blocks without the marker
line are snippets and are not run.

Exit status: 0 when every example passes; 1 when any fails or when no suitable JDK is found.

Usage: python3 tools/check-java-examples.py
"""
import glob
import os
import re
import subprocess
import sys
import tempfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

MARKER = re.compile(r'^// File: (\w+)\.java \(runnable, Java (\d+)\)\s*$')
FENCE = re.compile(r'^```(\S*)\s*$')


def java_major():
    try:
        out = subprocess.run(['java', '-version'], capture_output=True, text=True, timeout=30).stderr
    except (OSError, subprocess.TimeoutExpired):
        return None, ''
    m = re.search(r'version "(\d+)(?:\.(\d+))?', out)
    if not m:
        return None, out.strip()
    major = int(m.group(1))
    if major == 1 and m.group(2):            # old "1.8" style
        major = int(m.group(2))
    return major, out.splitlines()[0] if out else ''


def blocks(path):
    """Yield (language, start_line, body_lines, text_before_next_block) for every fenced block."""
    lines = open(path, encoding='utf-8').read().split('\n')
    found = []
    i = 0
    while i < len(lines):
        m = FENCE.match(lines[i])
        if m and m.group(1):
            lang, start, body = m.group(1), i + 1, []
            i += 1
            while i < len(lines) and lines[i].strip() != '```':
                body.append(lines[i])
                i += 1
            found.append([lang, start, body, None, i + 1])   # i is the closing fence
        i += 1
    for k, b in enumerate(found):
        end = b[4]
        nxt = found[k + 1][1] - 1 if k + 1 < len(found) else len(lines)
        b[3] = '\n'.join(lines[end:nxt - 1]) if k + 1 < len(found) else ''
    return found


major, version_line = java_major()
if major is None:
    print('java examples: FAILED, no working `java` command found (install a JDK, for example Java 21)')
    sys.exit(1)

passed = failed = varies = 0
files = sorted(glob.glob('notes/**/*.md', recursive=True))
for f in files:
    bl = blocks(f)
    for k, (lang, start, body, between, _) in enumerate(bl):
        if lang != 'java' or not body:
            continue
        m = MARKER.match(body[0])
        if not m:
            continue
        name, needed = m.group(1), int(m.group(2))
        where = f'{f}:{start}'
        if major < needed:
            failed += 1
            print(f'FAIL  {where} {name}.java needs Java {needed}, found {version_line}')
            continue
        expect_exact = '**Expected output**' in between
        expect_varies = '**Example output**' in between
        nxt = bl[k + 1] if k + 1 < len(bl) else None
        if not (expect_exact or expect_varies) or not nxt or nxt[0] != 'text':
            failed += 1
            print(f'FAIL  {where} {name}.java has no "**Expected output**" or "**Example output**" text block after it')
            continue
        with tempfile.TemporaryDirectory() as tmp:
            src = os.path.join(tmp, name + '.java')
            open(src, 'w', encoding='utf-8').write('\n'.join(body) + '\n')
            try:
                run = subprocess.run(['java', name + '.java'], cwd=tmp, capture_output=True, text=True, timeout=120)
            except subprocess.TimeoutExpired:
                failed += 1
                print(f'FAIL  {where} {name}.java did not finish within 120 seconds')
                continue
        output = (run.stdout + run.stderr).rstrip('\n')
        if run.returncode != 0:
            failed += 1
            print(f'FAIL  {where} {name}.java exited with status {run.returncode}:\n{output}')
            continue
        if expect_varies:
            varies += 1
            passed += 1
            print(f'ok    {where} {name}.java ran (output varies, not compared)')
            continue
        got = [l.rstrip() for l in output.split('\n')]
        want = [l.rstrip() for l in nxt[2]]
        if got == want:
            passed += 1
            print(f'ok    {where} {name}.java')
        else:
            failed += 1
            print(f'FAIL  {where} {name}.java output differs from the expected output')
            for i in range(max(len(got), len(want))):
                g = got[i] if i < len(got) else '<missing>'
                w = want[i] if i < len(want) else '<missing>'
                if g != w:
                    print(f'      line {i + 1}: expected {w!r}, got {g!r}')

print(f'java examples: {passed + failed} run with {version_line}, {passed} passed '
      f'({varies} with varying output, not compared), {failed} failed')
sys.exit(1 if failed else 0)

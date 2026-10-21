// Parses every Mermaid diagram in the notes with the Mermaid version Kenmora uses.
// Usage: KENMORA_WEB=<kenmora web folder> node tools/mermaid-check.mjs
import { readFileSync, readdirSync, statSync } from 'node:fs';
import { dirname, join, relative } from 'node:path';
import { fileURLToPath } from 'node:url';

const web = process.env.KENMORA_WEB;
if (!web) throw new Error('Set KENMORA_WEB to Kenmora\'s web folder');
const ROOT = join(dirname(fileURLToPath(import.meta.url)), '..', 'notes');

const { JSDOM } = await import(join(web, 'node_modules/jsdom/lib/api.js'));
const dom = new JSDOM('<!doctype html><html><body></body></html>');
globalThis.window = dom.window;
globalThis.document = dom.window.document;
const mermaid = (await import(join(web, 'node_modules/mermaid/dist/mermaid.core.mjs'))).default;

const files = [];
(function walk(d) {
  for (const n of readdirSync(d)) {
    const p = join(d, n);
    statSync(p).isDirectory() ? walk(p) : n.endsWith('.md') && files.push(p);
  }
})(ROOT);

let ok = 0, failed = 0;
for (const f of files.sort()) {
  const blocks = [...readFileSync(f, 'utf8').matchAll(/```mermaid\n([\s\S]*?)```/g)].map((m) => m[1]);
  for (const [i, b] of blocks.entries()) {
    try { await mermaid.parse(b); ok++; }
    catch (e) { failed++; console.log(`FAIL ${relative(ROOT, f)} diagram ${i + 1}: ${String(e.message || e).split('\n')[0]}`); }
  }
}
console.log(`mermaid: ${ok} diagrams OK, ${failed} failed`);
process.exit(failed ? 1 : 0);

// Renders every note with Kenmora's own Markdown pipeline and reports renderer warnings.
// Run from Kenmora's web folder so its imports resolve:
//   cd "$KENMORA_WEB" && npx vite-node <repo>/tools/render-check.ts
// Needs KENMORA_WEB (Kenmora's web folder) and LEARNING_NOTES (this repo's notes folder).
import { readFileSync, readdirSync, statSync } from 'node:fs';
import { join, relative } from 'node:path';

const web = process.env.KENMORA_WEB;
const ROOT = process.env.LEARNING_NOTES;
if (!web || !ROOT) throw new Error('Set KENMORA_WEB and LEARNING_NOTES');
const { renderDocument } = await import(join(web, 'src/renderer/core/pipeline'));

const files: string[] = [];
(function walk(d: string) {
  for (const n of readdirSync(d)) {
    const p = join(d, n);
    statSync(p).isDirectory() ? walk(p) : n.endsWith('.md') && files.push(relative(ROOT, p));
  }
})(ROOT);

const resolver = {
  findDocument: (c: readonly string[]) => c.find((x) => files.includes(x)) ?? null,
  findAsset: () => null,
  findAssetByFileName: () => null,
};

let warned = 0;
for (const f of files.sort()) {
  const r = await renderDocument(readFileSync(join(ROOT, f), 'utf8'), { path: f, resolver });
  warned += r.warnings.length;
  if (r.warnings.length) console.log(`WARN ${f}: ${r.warnings.join('; ')}`);
}
console.log(`render: ${files.length} files, ${warned} warnings`);
process.exit(warned ? 1 : 0);

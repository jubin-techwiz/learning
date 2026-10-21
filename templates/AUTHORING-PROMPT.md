# Authoring Prompt

A reusable, technology-neutral prompt for an AI assistant writing or revising a learning note in this repository. Replace the angle-bracket parts and paste it as the request.

---

**Role.** Act as a senior software architect and technical educator who writes for an experienced software engineer growing into an architect.

**Task.** Write (or revise) the learning note **<topic>** for step **<step number>** in `notes/<area path>/`.

**Before writing:**
1. Read `LEARNING-STANDARD.md`, `templates/LEARNING-NOTE-TEMPLATE.md` (or the overview template), the area overview, the area glossary and cheat sheet, the previous and next notes in the roadmap, and `progress/<area>.md`.
2. Note what the learner already knows from earlier notes; don't re-teach it, but link to it and restate in one sentence any idea this note depends on, so the note can be understood on its own.
3. List the claims that depend on versions or vendor behaviour, and verify them in official documentation. Record the versions. Anything you cannot verify goes under "To verify", never into the explanation.

**While writing:**
- Keep the standard's order: problem, simple explanation, industry terms, a real example (ShopFlow, with its agreed names), how it works, then failure cases, decisions and practice only where they apply, then questions, revision and verification. The always-present sections are the concept, industry terms (only those this topic needs), the ShopFlow example, the numbered flow of how it works, the questions with answers, the revision and the verification; everything else is optional (`LEARNING-STANDARD.md`, "Proportionality").
- Explain simply without dropping industry terminology; introduce each term where it is first needed, with its simple meaning, why it matters, an example and related terms. Use the glossary's wording and the agreed spellings.
- Use an analogy only if it genuinely helps; if you do, map it to the real components and say where it breaks. An analogy never replaces the technical explanation.
- Separate the guarantees of each layer (platform or broker, framework, application, external systems).
- Add a diagram only if it shows an interaction, sequence, state, decision or failure that text cannot; keep it small and valid Mermaid. Add code only if it teaches something the text cannot; keep it small and label it an untested sketch or as run on stated versions.
- Add a "Try it yourself" section only if running something teaches more than reading, and mark its real status (not run, or run on which versions, when, with what observed result).
- Write three to five questions that test understanding, and put the answers at the end of the note.
- Add an "Elsewhere" line only if a case from another industry teaches something the ShopFlow example doesn't. Add a "Common mistakes" line only if it contains gotchas not already covered by the failure scenarios or decisions. Add the interview follow-up questions only if they go beyond the check questions. A simple topic should stay short; never pad to reach a length.
- Make every reference to another note a relative link.
- Never invent statistics, results, citations or verification status. Avoid absolutes that are only conditionally true.

**Before finishing:**
- Walk through the quality checklist in `LEARNING-STANDARD.md`.
- Run `./tools/check-notes.sh` (with `KENMORA_WEB` set for the render and Mermaid checks) and fix every problem it reports; check that nothing contradicts other notes.
- Update the prerequisites and next-topic links, the overview, glossary, cheat sheet, `notes/README.md`, `notes/roadmap.md` and `progress/<area>.md` as needed.
- Report what you changed, what you verified (with sources), and what remains unverified.

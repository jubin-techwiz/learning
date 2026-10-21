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
- Follow the standard's progression: problem, simple explanation, industry terms, a real example (ShopFlow, with its agreed names), how it works, failure cases, trade-offs, practice, revision.
- Explain simply without dropping industry terminology; introduce each term where it is first needed, with its simple meaning, why it matters, an example and related terms. Use the glossary's wording and the agreed spellings.
- Map any analogy to the real components and say where it breaks.
- Separate the guarantees of each layer (platform or broker, framework, application, external systems).
- Use diagrams only to show interactions, sequences, states, decisions or failures; keep them small and valid Mermaid.
- Keep code small and explained, and label it as an untested sketch or as run on stated versions.
- Add a "Try it yourself" section only if running something teaches more than reading; mark its real status.
- Write three to five questions that test understanding, and put the answers at the end of the note. Optionally add three to five interview follow-up questions with one-line answers in the revision section.
- Add an "Elsewhere" line, a "Common mistakes" line, an analogy, a diagram or code only when each genuinely helps this topic; a simple topic should stay short. Never pad to reach a length.
- Make every reference to another note a relative link.
- Never invent statistics, results, citations or verification status. Avoid absolutes that are only conditionally true.

**Before finishing:**
- Walk through the quality checklist in `LEARNING-STANDARD.md`.
- Run `./tools/check-notes.sh` (with `KENMORA_WEB` set for the render and Mermaid checks) and fix every problem it reports; check that nothing contradicts other notes.
- Update the prerequisites and next-topic links, the overview, glossary, cheat sheet, `notes/README.md`, `notes/roadmap.md` and `progress/<area>.md` as needed.
- Report what you changed, what you verified (with sources), and what remains unverified.

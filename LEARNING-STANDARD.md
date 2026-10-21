# Learning Standard

How every learning note in this repository is written, checked and maintained. It applies to every topic (Kafka, RabbitMQ, Java, Spring Boot, AWS, Kubernetes, security, observability, distributed systems, architecture patterns) so new topics never need a new format.

- **Template for a topic note:** [`templates/LEARNING-NOTE-TEMPLATE.md`](templates/LEARNING-NOTE-TEMPLATE.md)
- **Template for an area or series overview:** [`templates/AREA-OVERVIEW-TEMPLATE.md`](templates/AREA-OVERVIEW-TEMPLATE.md)
- **Authoring prompt for an AI assistant:** [`templates/AUTHORING-PROMPT.md`](templates/AUTHORING-PROMPT.md)

The reader is an experienced software engineer growing into a software architect. Notes are patient and concept-first, but never shallow: they keep the industry vocabulary, the important conditions and the real trade-offs.

---

## 1. Repository layout

| Folder | Holds | Read in Kenmora? |
|---|---|---|
| `notes/` | The learning notes, numbered in learning order | **Yes, this folder only** |
| `labs/` | Optional runnable demos, one per area, each with its own README | No |
| `progress/` | The learner's progress per area (done, gaps, next) | No |
| `templates/` | Templates and the authoring prompt | No |
| `LEARNING-STANDARD.md` | This document | No |

Notes never reference a lab's path, scripts or commands; a lab never depends on the notes. Both may describe the same idea.

## 2. Numbering, naming and note types

- **Areas:** `notes/NN-<area>/`, numbered in the order the learner studies them (see `notes/roadmap.md`).
- **Notes:** `NN-<topic-slug>.md`, kebab-case, numbered in learning order. The step number is the path: `02-messaging/02-kafka/05-offset-commits-delivery-guarantees.md` is **step 2.2.5**.
- **Series:** a subject that needs many notes gets its own folder inside an area (`02-kafka/`). Small areas stay flat.
- **Reserved numbers:** `01-...-overview.md` opens every area and series; `98-...-glossary.md` is an area's glossary; `99-...-cheat-sheet.md` is an area's revision sheet. Numbers 90 and above are shown as revision material and sort last.
- **Gaps are allowed:** a number equals a roadmap step, so a missing number simply means that step is not written yet.
- Folders are created only when their first note is written. Renaming a note changes its identity in Kenmora, so prefer adding over renaming.

| Note type | Purpose | Template |
|---|---|---|
| Area overview (`01-...`) | Map of the area: the problem family, how topics relate, learning order | Area overview |
| Series overview (`01-...` inside a series) | Map of one subject from fundamentals to advanced | Area overview |
| Topic note | One concept, taught fully | Learning note |
| Glossary (`98-...`) | Canonical definitions of the area's terms | Free form: one term per row |
| Cheat sheet (`99-...`) | One-page revision of the area | Free form: tables, rules, short answers |

## 3. The structure of a topic note

Every topic note follows this progression:

**Problem → simple explanation → industry terms → real example → how it works → failure cases → trade-offs → practice → revision.**

| # | Section | Required? | What it does |
|---|---|---|---|
| | Front matter, title, position line | Yes | `title`, `tags`, `category`, `level`, `updated`; step, level and prerequisites |
| 1 | **The concept in simple English** | Yes | What is it? Why do we need it? When would I meet it? Memory hook |
| 2 | **Mental model** | When an analogy genuinely helps | The analogy, a mapping table to the real components, and where the analogy stops being accurate |
| 3 | **Industry terms** | Yes | Only the terms this topic needs, in the five-column format (section 5) |
| 4 | **In a real system (ShopFlow)** | Yes | Business problem first, then how the concept solves it, then what it costs; a "before" diagram only where a picture helps; optionally an **Elsewhere** line with short cases from other industries, when they add understanding |
| 5 | **How it works** | Yes | A numbered flow (what starts it, who takes part, what happens on success and failure); a diagram only if it shows an interaction, sequence, state or decision; "who guarantees what" when several layers are involved; code only if it teaches something the text can't |
| 6 | **Failure scenarios** | When failures are part of the topic | One to three concrete cases in the five-part format (section 9) |
| 7 | **Architectural decisions** | For substantial concepts | When to use, when not, alternatives, and only the dimensions that matter; an optional one-line **Common mistakes** list for gotchas not covered as failure scenarios |
| 8 | **Try it yourself** | Optional | A small predict-then-run experiment, with honest status |
| 9 | **Check your understanding** | Yes | Three to five questions; answers at the end of the note |
| 10 | **Revision** | Yes | Remember, 15-second answer, 60-second answer, likely follow-up questions (optional), architect's takeaway, next topic |
| 11 | **Verification and sources** | Yes | Confirmed facts with sources and versions; assumptions; items to verify |
| | **Answers** | Yes | Answers to section 9, kept at the very end |

**Numbering when sections are left out.** Number the sections that are present, in this order, without gaps. A note without "Try it yourself" therefore has Check your understanding as section 8, Revision as 9 and Verification as 10. Keep the section names unchanged so notes stay easy to scan.

**Proportionality.** The order of the progression is fixed; the amount in each step is not. A small topic may need no analogy, no diagram, no code, no failure scenarios and no "Elsewhere" line; say what matters and stop. The sections that are always present are: the concept in simple English, industry terms (only those the topic needs), the ShopFlow example, the numbered flow of how it works, check your understanding, revision and verification. Everything else appears only when it earns its place. Never add a diagram, analogy, code block, industry example or extra paragraph just to fill a slot, or to reach a word count.

**Length.** Aim for the shortest note that gives complete understanding with its important conditions. As a rough ceiling, up to about 2,000 words of prose (not counting code, diagrams and tables); many topics need far fewer. The number is never a target, and a short note is not a defect. Cut repetition and generic advice before cutting explanation, failure cases or conditions.

## 4. How to explain a concept

1. **Start with the problem**, in plain words, before any definition.
2. **What / why / when** in one or two sentences each. Never open with a sentence copied from documentation.
3. **Then the formal term.** "This is called a *consumer group*."
4. **One idea per paragraph**, short sentences, everyday words.
5. **Introduce complexity progressively.** The core idea first; edge cases after the reader understands the basics.
6. **Simple is not the same as simplified.** If a statement is only true under a condition, state the condition ("with `acks=all` and `min.insync.replicas=2`"). Avoid absolutes ("never", "always", "guarantees") unless they are literally true.

**Analogies** are optional. When used, they must be followed by a mapping table (analogy → real component) and a "where the analogy breaks" line. An analogy never replaces the technical explanation.

## 5. Industry terminology

Industry terms are part of what an architect must know. Never remove a term to make a note simpler; explain it instead.

- Introduce a term **where the reader first needs it**, and explain acronyms on first use.
- In section 3, use this format, only for terms the note needs:

| Term | Simple meaning | Why it matters | Example | Related terms |
|---|---|---|---|---|

- The area glossary (`98-...-glossary.md`) holds the **canonical** definition. A note may shorten it but must not contradict it.
- Use the same name for the same thing everywhere. Agreed spellings:
  - **dead-letter topic (DLT)** for Kafka; **dead-letter queue (DLQ)** for queue brokers and Kafka Connect, which uses that name.
  - **exactly-once** (hyphenated) as an adjective; "exactly once" as an adverb.
  - **at-least-once** and **at-most-once** likewise.
  - **acknowledgement (ack)**; **consumer group**; **in-sync replicas (ISR)**; **transactional outbox**.

## 6. Examples

**ShopFlow** is the running example: an illustrative online shop. Use the same names everywhere:

| In prose | In code, configuration and diagrams | Role |
|---|---|---|
| Order Service | `order-service` | Takes orders, publishes `OrderPlaced` |
| Payment Service | `payment-service` | Charges cards through an external payment provider |
| Inventory Service | `inventory-service` | Reserves stock |
| Notification Service | `notification-service` | Sends emails and push messages |
| Analytics Service | `analytics-service` | Builds reports from the event stream |

Event names are past tense (`OrderPlaced`, `PaymentFailed`, `StockReserved`, `OrderCancelled`); command names are imperative (`ChargeCard`).

- **Business problem first**, then the technical solution, then the trade-offs it introduces.
- **Real-world examples** come from other domains (payments, ride-hailing, streaming, IoT), described generically. Never claim how a named company works internally without a source.
- **Never invent numbers**: no made-up performance figures, adoption claims or "measured" results. Arithmetic examples ("50 GB per day × 7 days × 3 copies") are fine when labelled as examples.
- **Label every example** as one of: *conceptual illustration*, *simplified example*, *runnable (run on version X, with result Y)*, or *verified recommendation (source)*.

## 7. Diagrams

Use a diagram when it shows an **interaction, a sequence, a state change, a decision or a failure** that text alone makes hard to follow. Never decorate.

- Mermaid only: `flowchart`, `sequenceDiagram`, `stateDiagram-v2`.
- Keep diagrams small (about 12 nodes or fewer). Plain labels; avoid special characters that break parsers.
- Topic notes usually have a "before" diagram (the problem) and a "how it works" diagram (the solution or failure path).
- Every diagram must parse; check it with a Mermaid validator before finishing.

## 8. Code and configuration

- Small, focused snippets with a clear learning purpose; explain the important lines in comments or the text.
- State the status above every block: *untested sketch* or *run on <versions>*. Never imply code was run when it wasn't.
- Mark names that change between versions and say "check your version".

## 9. Failure scenarios and who guarantees what

Write each failure scenario in five parts:

1. **What happened?**
2. **Why did it happen?**
3. **What would users or the business see?**
4. **What are the solutions?**
5. **What do those solutions cost?**

Where several layers are involved, include a **"who guarantees what"** table, separating:

| Layer | Example (Kafka) |
|---|---|
| Broker and protocol | Replication, ordering within a partition, transactions |
| Framework | Spring Kafka's commit modes, retries and error handlers |
| Application | Idempotency, deduplication, choosing keys, handling send results |
| External systems | Databases, email providers and HTTP APIs, which Kafka's guarantees do not cover |

Never let a guarantee from one layer be read as covering another.

## 10. Architectural decisions

For substantial concepts, give the decision criteria: **when to use, when not to use, alternatives**, and only those dimensions that change the decision (reliability, ordering, performance and scale, operational complexity, security, observability, cost). Compare technologies against a **specific requirement and workload**, never as universally better or worse.

## 11. Hands-on practice

- Practice is **optional**. Add it only when running something teaches more than reading.
- **In a note:** a short "Try it yourself" section: prerequisites, commands, *predict first*, what to observe, one deliberate failure, cleanup, and an honest **status** ("not run in this repository" until it has been).
- **A lab:** only when a larger runnable demo is worth it (usually on request). One lab per area in `labs/NN-<area>/<area>-demo/`, documented with the sections required in `labs/README.md`: objective, prerequisites with versions, configuration, commands, expected output, what to observe, a failure experiment, troubleshooting, cleanup and questions.
- Prefer local, free infrastructure (Docker Compose or the existing lab setup). Never claim a demo passed unless it was run.

## 12. Verifying technical claims

1. **Sources, in order of preference:** official project documentation (Apache Kafka, Spring, vendor docs), specifications and KIPs, then reputable engineering articles. Blogs alone are "directional".
2. **Record the version** a behaviour or default applies to. Defaults change between versions; never present one as universal.
3. **Separate** what is verified, what is an assumption, and what is an illustrative example.
4. **Unverified claims stay out of the teaching sections.** Write "check your version's default" there, and list the item under *To verify* in the verification section.
5. **Correct absolutes.** Typical traps: "at-least-once means the operation always succeeds", "exactly-once covers my database and HTTP calls", "the database update and the email are atomic", "retries keep business order", "this default holds in every version".
6. **Verification section format:** checked date and method; *Confirmed* (with source and version); *Assumptions and illustrations*; *To verify*; *Decision rule*.

## 13. Exercises, answers and revision

- **Check your understanding:** three to five questions, mixing conceptual, "what happens if...?", troubleshooting and design-decision questions. They test understanding, not recall of definitions.
- **Answers** go in a final "Answers" section at the end of the note, so the reader can try first. Answers explain *why*.
- **Revision:** *Remember* (three to five lines), *Explain in 15 seconds*, *Explain in 60 seconds* (with one qualification or trade-off), optionally *Likely follow-up questions* (three to five interview-style questions, each with a one-line answer, not repeating "Check your understanding"), *Architect's takeaway*, *Next topic* (a link).
- **Interview practice** lives in the revision section and the cheat sheet; it complements understanding, never replaces it.
- Every area has a **cheat sheet** (`99-...`) and, once the vocabulary is large, a **glossary** (`98-...`).

## 14. Markdown and Kenmora conventions

- Front matter: `title` and `tags` are shown by Kenmora; `category`, `level` (`fundamentals`, `intermediate`, `advanced`) and `updated` (quoted `"YYYY-MM-DD"`) are kept for maintenance.
- Headings: one `#` title; numbered `##` sections as in section 3.
- Callouts: `> [!NOTE]`, `> [!TIP]`, `> [!IMPORTANT]`, `> [!WARNING]`, `> [!CAUTION]`.
- Code fences always have a language tag.
- Links are **relative** and stay inside `notes/` (`../`, `../../` as needed). No `[[wikilinks]]`, no raw HTML, no inline SVG.
- Every note ends with links back to its overview, the index and the roadmap.
- **Every reference to another note is a link**, for example `[2.2.11](11-ordering-partition-planning-hot-partitions.md)`, never a bare step number. Plain numbers are allowed only for notes not written yet.
- **Understandable alone:** a note may point to a prerequisite, but it briefly restates (in a sentence) any idea it depends on, so the learner doesn't have to open several notes to follow it.

## 15. Navigation and maintenance

When a note is added or changed, update together:

1. The note's **prerequisites** (position line) and **next topic** (revision section).
2. The **area or series overview** table.
3. The area **glossary** and **cheat sheet**, if terms or rules changed.
4. `notes/README.md` (the index) and `notes/roadmap.md` (status and "Do next").
5. `progress/<area>.md`, when a lesson is completed.

If a file moves, update every internal link and run `./tools/check-notes.sh` (see `tools/README.md`).

## 16. Quality checklist

Before a note is considered done:

- [ ] Opens with what, why, when and a memory hook, in plain English.
- [ ] Every important term is introduced where needed, uses the agreed spelling, and matches the glossary.
- [ ] Any analogy has a mapping table and a "where it breaks" line.
- [ ] The ShopFlow scenario states the business problem before the solution.
- [ ] The "how it works" part says what starts the operation, who takes part, how state moves, who guarantees what, and what happens on success and failure.
- [ ] Failure scenarios use the five-part format; layers of guarantees are not mixed up.
- [ ] Decisions are compared against a specific requirement; no "always better" claims.
- [ ] No unverified fact in the teaching sections; versions recorded for defaults; no invented numbers.
- [ ] Code is small, explained and labelled with its status.
- [ ] Three to five questions, with answers only at the end.
- [ ] Revision section complete, with a working "next topic" link.
- [ ] `./tools/check-notes.sh` passes (with `KENMORA_WEB` set): links resolve, structure is complete, diagrams parse, the page renders without warnings.
- [ ] Nothing contradicts another note in the repository.
- [ ] Overview, glossary, cheat sheet, index, roadmap and progress updated.

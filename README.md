# Learning

A long-term place to learn software engineering and architecture topics, one after another, and to revise them later. Resilience comes first, then Kafka from fundamentals to advanced, then design patterns and microservices, followed by data, APIs, security, cloud, observability, delivery and architect skills.

The principle: **the documentation stands on its own, and hands-on demo apps are optional.**

## What is where

| Folder | What it holds | Imported into Kenmora? |
|---|---|---|
| [`notes/`](notes/README.md) | The learning notes: simple, structured, numbered in learning order. Start at `notes/roadmap.md` | **Yes, this folder only** |
| [`labs/`](labs/README.md) | Optional runnable demo apps, one per area, each with its own README | No |
| `progress/` | Learner tracking for the coach: what is done, gaps, next lesson | No |
| [`LEARNING-STANDARD.md`](LEARNING-STANDARD.md) | The rules every note follows: structure, explanations, terms, examples, diagrams, verification, checklist | No |
| [`templates/`](templates/) | A note template, an area-overview template and a reusable authoring prompt | No |
| [`tools/`](tools/README.md) | Checks for links, note structure, Kenmora rendering and Mermaid diagrams (`./tools/check-notes.sh`) | No |

```
learning/
  README.md          this file
  LEARNING-STANDARD.md   how notes are written (read before writing one)
  templates/         note and overview templates, authoring prompt
  tools/             documentation checks (check-notes.sh)
  notes/             docs (the only folder for Kenmora)
    roadmap.md
    01-resilience/ ...
  labs/              optional demos, numbered like notes/
    01-resilience/resilience-demo/
  progress/
```

## How to study a topic

1. Open `notes/roadmap.md` and pick the next item under **Do next**.
2. Read the area's overview note, then the topic note.
3. Answer the note's "Check your understanding" questions before reading the answers at the end.
4. If the note has a "Try it yourself" experiment, or a demo exists for the area, run it. It is never required.
5. Revise later from the area's cheat sheet and glossary.

## Reading in Kenmora

Import the `notes/` folder only. Notes use relative links, callouts, Mermaid diagrams and front matter, all supported by Kenmora. Nothing in `notes/` links outside that folder.

## Numbering rule

Folder and file numbers form the step number, so alphabetical order is learning order:

- `notes/01-resilience/05-circuit-breaker.md` is step **1.5**
- `notes/02-messaging/02-kafka/05-offset-commits.md` would be step **2.2.5** (track 2, Kafka series, step 5)
- `labs/02-messaging/kafka-demo/` is the optional demo for track 2

## Adding a new area or series

1. Create `notes/NN-<area>/` and write its `01-<area>-overview.md` first (the area index and how topics relate). For a big subject such as Kafka, add a series folder `NN-<series>/` with its own overview.
2. Add notes as `NN-<topic>.md` in learning order. Start from `templates/LEARNING-NOTE-TEMPLATE.md` and follow `LEARNING-STANDARD.md`; when an AI assistant writes the note, give it `templates/AUTHORING-PROMPT.md`.
3. Update the area overview, glossary, cheat sheet, `notes/README.md` and `notes/roadmap.md` together.
4. Only if running something helps: create `labs/NN-<area>/<area>-demo/`, give it a port from the registry in `labs/README.md`, and write its README. Notes never mention the lab.

## Rules that keep it tidy

- Notes are self-contained: example code and real results are written inside the note.
- Labs document themselves and do not depend on the notes.
- Only record results that were actually observed, with versions.
- Each note has a Verification and sources section that says what was checked, against which versions, and what is still unverified; read it before relying on the note.
- Build output (`target/`, `node_modules/`) and secrets are never committed; see `.gitignore`.

## Version control

This folder is a git repository. Its `origin` remote is the GitHub repository `jubin-techwiz/learning`, reached through the `github.com-work` SSH alias (the machine has two GitHub accounts, so use that alias, not plain `github.com`). Work happens on `main`; commit and push only when you decide to (an assistant should not commit or push unless asked).

**Visibility.** When last checked (2026-10-21, with `gh repo view jubin-techwiz/learning --json visibility`) the repository was **public**, so everything pushed to it, including these notes, is readable by anyone. Visibility is a GitHub setting owned by the repository owner and this README does not change it; re-check it before assuming otherwise, and keep secrets and private material out of the repository either way.

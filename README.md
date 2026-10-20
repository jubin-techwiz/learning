# Learning

A long-term place to learn software engineering and architecture topics, one after another, and to revise them later. Resilience comes first, then Kafka from fundamentals to advanced, then design patterns and microservices, followed by data, APIs, security, cloud, observability, delivery and architect skills.

The principle: **the documentation stands on its own, and hands-on demo apps are optional.**

## What is where

| Folder | What it holds | Imported into Kenmora? |
|---|---|---|
| [`notes/`](notes/README.md) | The learning notes: simple, structured, numbered in learning order. Start at `notes/roadmap.md` | **Yes, this folder only** |
| [`labs/`](labs/README.md) | Optional runnable demo apps, one per area, each with its own README | No |
| `progress/` | Learner tracking for the coach: what is done, gaps, next lesson | No |

```
learning/
  README.md          this file
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
3. Answer the note's Quick Q&A from memory.
4. If a demo exists for the area, run it from its README and watch the behaviour the note describes. It is never required.
5. Revise later from the area's cheat sheet.

## Reading in Kenmora

Import the `notes/` folder only. Notes use relative links, callouts, Mermaid diagrams and front matter, all supported by Kenmora. Nothing in `notes/` links outside that folder.

## Numbering rule

Folder and file numbers form the step number, so alphabetical order is learning order:

- `notes/01-resilience/05-circuit-breaker.md` is step **1.5**
- `notes/02-messaging/02-kafka/05-offset-commits.md` would be step **2.2.5** (track 2, Kafka series, step 5)
- `labs/02-messaging/kafka-demo/` is the optional demo for track 2

## Adding a new area or series

1. Create `notes/NN-<area>/` and write its `01-<area>-overview.md` first (the area index and how topics relate). For a big subject such as Kafka, add a series folder `NN-<series>/` with its own overview.
2. Add notes as `NN-<topic>.md` in learning order, using the standard note sections.
3. Update the area overview, `notes/README.md` and `notes/roadmap.md` together.
4. Only if running something helps: create `labs/NN-<area>/<area>-demo/`, give it a port from the registry in `labs/README.md`, and write its README. Notes never mention the lab.

## Rules that keep it tidy

- Notes are self-contained: example code and real results are written inside the note.
- Labs document themselves and do not depend on the notes.
- Only record results that were actually observed, with versions.
- Each note has an Industry check section; verify it before relying on the note.
- Build output (`target/`, `node_modules/`) and secrets are never committed; see `.gitignore`.

## Version control

This folder is a local git repository (no commits and no remote yet). Commit when you want a checkpoint of your notes.

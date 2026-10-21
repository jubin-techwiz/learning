# Labs

Optional hands-on demo apps that go with the learning notes. A topic does not need a lab; a lab exists only when running something helps to understand the topic.

The labs are separate from the notes on purpose. The notes (`../notes/`) are standalone and are the only folder imported into Kenmora. Each lab documents itself in its own README and does not depend on the notes.

## Layout

The folder numbers mirror the notes, so a lab is easy to find from its topic:

```
labs/
  01-resilience/resilience-demo/       <- goes with notes/01-resilience/
  02-messaging/kafka-demo/             <- not created; only if asked for
```

**One lab per area.** A lab grows one small flow per topic (for example the resilience demo has a payment flow for the circuit breaker and a stock flow for the bulkhead). This keeps the number of projects small and lets patterns be combined.

## Lab index

| Area | Lab | Covers | Port | Run | Needs |
|---|---|---|---|---|---|
| 01 Resilience | [resilience-demo](01-resilience/resilience-demo/README.md) | Circuit breaker, bulkhead | 8085 | `mvn spring-boot:run`, then `./demo.sh` and `./demo-bulkhead.sh` | Java 17+, Maven 3.9+ |

There is no messaging lab. The Kafka notes include small "Try it yourself" experiments that need only a local broker and the Kafka command-line tools, so a separate project isn't needed for now.

## Port registry

Every lab uses its own port, so several labs can run together. Add a row here when a lab is created.

| Port | Lab |
|---|---|
| 8085 | resilience-demo |
| 8086 | (next lab) |
| 8087 | (reserved) |

Infrastructure used by a lab (for example Kafka or a database) is started by that lab's own README steps, and its ports are listed in that lab's README.

## Conventions every lab follows

1. **Its own README** with the sections defined for demo READMEs: purpose, what it demonstrates, prerequisites, quick start, project layout, endpoints, configuration, scenarios with real example output, tests, experiments, troubleshooting, known limits, possible next additions.
2. **Only documented what was run.** Example output and test counts come from real runs.
3. **Self-contained.** No absolute paths, no links outside the lab folder, no secrets, no committed build output (`target/`, `node_modules/`).
4. **Scripts explain themselves** with a header comment: what it shows, what must be running, how to call it.
5. **Tests included**, and the README states how many and how long they take.
6. **Everything about the lab changes together**: when a flow, endpoint, setting or test changes, the README changes in the same step.

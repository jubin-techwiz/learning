---
title: Docker Cheat Sheet
tags: [docker, cheat-sheet, revision]
category: cloud-and-containers
level: fundamentals
updated: "2026-10-22"
---

# Docker Cheat Sheet

**Cloud and containers · Docker · Revision** · Last updated 2026-10-22

One page to revise the Docker series, [9.2.1](01-docker-overview.md) to [9.2.9](09-container-security.md). The numbers are the ones observed in the lessons (Docker Engine 28.4.0, Java 21); yours will differ a little.

> [!TIP]
> **The whole series in one line:** a container is an ordinary process with a restricted view and limits, started from an immutable image; build small images, keep state and secrets outside them, let the process shut down on `SIGTERM`, and give it the least privilege.

## The ideas in one table

| Idea | What to remember | Lesson |
|---|---|---|
| Container | A process with its own namespaces (what it sees) and cgroups (what it may use) on the **shared host kernel** | [9.2.2](02-containers-isolated-processes.md) |
| Limits | `--memory`, `--cpus`, `--pids-limit`; going over memory kills the process (exit 137 when it was PID 1) | [9.2.2](02-containers-isolated-processes.md) |
| Image | Stack of immutable layers plus metadata; a change rebuilds that step and all later ones | [9.2.3](03-images-and-layers.md) |
| Layers never shrink | Deleting a file in a later layer leaves it in the earlier one (53.4 MB versus 13.3 MB when done in one step) | [9.2.3](03-images-and-layers.md) |
| Tag versus digest | A tag can move; a digest cannot. Pin what you ship | [9.2.3](03-images-and-layers.md) |
| Java image | Multi-stage build, non-root user, exec-form `ENTRYPOINT`; 555 MB naive, 286 MB multi-stage, 74.2 MB with `jlink` | [9.2.4](04-dockerfile-java-service.md) |
| JVM in containers | The JVM reads the container limit; the default heap is a fraction of it, so set `MaxRAMPercentage` deliberately | [9.2.4](04-dockerfile-java-service.md) |
| `.dockerignore` | Keeps the build context small (35.01 MB versus 157 B in the example) | [9.2.4](04-dockerfile-java-service.md) |
| Networks | User-defined networks give DNS names; the default bridge does not; `localhost` is the container itself | [9.2.5](05-networking-and-ports.md) |
| Published ports | Bind to all interfaces unless you write `127.0.0.1:`; only publish what outsiders need | [9.2.5](05-networking-and-ports.md) |
| Data | Volumes for data, bind mounts for source and config (`:ro`), `tmpfs` for scratch (it counts against memory) | [9.2.6](06-data-and-configuration.md) |
| Configuration | Environment variables for settings; secrets as files, not environment (visible in `docker inspect`) | [9.2.6](06-data-and-configuration.md) |
| Stopping | `docker stop` sends `SIGTERM`, waits 10 s, then `SIGKILL`; exit 143 is a clean stop, 137 a kill | [9.2.7](07-lifecycle.md) |
| Health and restarts | `HEALTHCHECK` is recorded but Docker does not restart on it; restart policies back off between attempts | [9.2.7](07-lifecycle.md) |
| Compose | One file for a multi-container stack on **one host**; `depends_on` with `service_healthy`; `down -v` also deletes volumes | [9.2.8](08-docker-compose.md) |
| Runtime hardening | Non-root, `--cap-drop ALL`, `no-new-privileges`, `--read-only` plus `--tmpfs`, limits | [9.2.9](09-container-security.md) |
| The Docker socket | Mounting it into a container is root on the host | [9.2.9](09-container-security.md) |
| Secrets in builds | `ARG`, `ENV` and deleted files stay in the image; use `RUN --mount=type=secret` | [9.2.9](09-container-security.md) |
| Scanning | Finds known CVEs only; scan every build and rebuild regularly | [9.2.9](09-container-security.md) |

## Commands worth knowing

| Goal | Command |
|---|---|
| Run with limits | `docker run -d --memory 256m --cpus 1 -p 127.0.0.1:8080:8080 IMAGE` |
| See what is inside an image | `docker history IMAGE` and `docker image inspect IMAGE` |
| See what a container changed | `docker diff CONTAINER` |
| Exit code and OOM flag | `docker inspect -f '{{.State.ExitCode}} {{.State.OOMKilled}}' CONTAINER` |
| Why is the build slow | Order the Dockerfile from rarely to often changing; check the cache lines in the build output |
| Validate a Compose file | `docker compose config` |
| Remove only your own things | Filter by label or name; never prune on a shared engine |

## A review checklist for an image and its run

1. Small, maintained base image, pinned version (and digest for releases).
2. Multi-stage build; no compiler or build tools in the final image.
3. Non-root user; exec-form `ENTRYPOINT` so the app gets `SIGTERM`; it shuts down gracefully within the stop timeout.
4. Memory limit set, and the JVM sized to it.
5. No secrets in `ARG`, `ENV` or layers; `.dockerignore` present.
6. Ports published only as needed, on loopback for local work.
7. Data in named volumes; configuration from the environment or mounted files.
8. Read-only root filesystem with `tmpfs`, all capabilities dropped, `no-new-privileges`.
9. No Docker socket mount, no `--privileged`.
10. Scanned in CI and rebuilt on a schedule.

## Interview questions

- **What is a container?** A process on the host kernel, isolated by namespaces and limited by cgroups, started from a layered image ([9.2.2](02-containers-isolated-processes.md)).
- **Container versus virtual machine?** A VM has its own kernel; containers share the host's, so they start faster and are lighter but isolate less ([9.2.2](02-containers-isolated-processes.md)).
- **Why is my image huge, and how do I shrink it?** Layers never shrink; use multi-stage builds, a smaller runtime, `jlink`, and `.dockerignore` ([9.2.3](03-images-and-layers.md), [9.2.4](04-dockerfile-java-service.md)).
- **Why was my Java container killed?** The heap plus non-heap memory exceeded the container limit; size with `MaxRAMPercentage` and leave headroom ([9.2.2](02-containers-isolated-processes.md), [9.2.4](04-dockerfile-java-service.md)).
- **Why does `localhost` not reach my database?** In a container it means the container itself; use the service name on a shared user-defined network ([9.2.5](05-networking-and-ports.md)).
- **Where does the data go when the container is removed?** Into the writable layer and gone, unless it is in a volume ([9.2.6](06-data-and-configuration.md)).
- **How do you do a graceful shutdown?** Run the app as PID 1 in exec form (or use `--init`), handle `SIGTERM`, finish in-flight work before the stop timeout ([9.2.7](07-lifecycle.md)).
- **Is Compose production?** It runs on a single host and is for local development and small setups; clusters need an orchestrator ([9.2.8](08-docker-compose.md)).
- **How do you secure a container?** Non-root, dropped capabilities, read-only filesystem, limits, no socket or privileged mode, build secrets, scanning and rebuilds ([9.2.9](09-container-security.md)).

Back to [the Docker series overview](01-docker-overview.md) · [the area overview](../01-cloud-and-containers-overview.md) · [the index](../../README.md) · [Roadmap](../../roadmap.md).

# Learning Progress Snapshot
- Subject: Docker (track 9, series 9.2, `notes/09-cloud-and-containers/02-docker/`)
- Goal: Package, run, connect, operate and secure a Java service in containers, and explain the trade-offs as an engineer and architect
- Status: Done on 2026-10-22 (9.2.1 to 9.2.9 and the cheat sheet written)
- Completed: 9.1 cloud and containers overview, 9.2.1 series overview, 9.2.2 containers (namespaces, cgroups, PID 1, OOM), 9.2.3 images and layers (cache, layers never shrink, digests), 9.2.4 Dockerfile for a Java service (multi-stage, jlink, JVM memory, .dockerignore), 9.2.5 networking and ports, 9.2.6 data and configuration, 9.2.7 lifecycle (signals, graceful shutdown, health checks, restarts), 9.2.8 Compose, 9.2.9 container security (capabilities, read-only, socket, build secrets, Trivy scan), 99 Docker cheat sheet; all hands-on with output captured from real runs
- Strong areas: not assessed (answers are included in the notes; none are collected)
- Gaps to revisit: none recorded; restart-policy timing differed from the docs' 10 s statement and is listed under "To verify" in 9.2.7
- Environment/setup: Docker Desktop on a Mac (Docker Engine 28.4.0). The same engine runs other containers of yours: examples use the `sf-learn-` prefix and the label `learning=notes`, and never prune or stop anything else. Trivy 0.76.1 was run as a container for scanning
- Last exercise and result: none collected; solutions are in the notes
- Next lesson: none in this series; continue with the Kubernetes series (done, see `progress/kubernetes.md`) or the cloud services track (9.4)

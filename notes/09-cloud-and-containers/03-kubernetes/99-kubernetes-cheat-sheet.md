---
title: Kubernetes Cheat Sheet
tags: [kubernetes, cheat-sheet, revision]
category: cloud-and-containers
level: fundamentals
updated: "2026-10-23"
---

# Kubernetes Cheat Sheet

**Cloud and containers · Kubernetes · Revision** · Last updated 2026-10-23

One page to revise the Kubernetes series, [9.3.1](01-kubernetes-overview.md) to [9.3.10](10-troubleshooting-day-two.md). The numbers are the ones observed in the lessons (Kubernetes v1.36.5 on Rancher Desktop); your cluster may differ.

> [!TIP]
> **The whole series in one line:** declare the desired state in Git; controllers keep it true; make every workload ready, bounded, named, secured and observable, and diagnose in a fixed order.

## The objects and what each is for

| Object | One-line purpose | Remember | Lesson |
|---|---|---|---|
| **Pod** | Containers sharing one IP and storage | Replaced, never repaired; new name and IP each time | [9.3.2](02-pods-replicasets-deployments.md) |
| **ReplicaSet** | Keeps N identical pods (by selector) | You rarely write one | [9.3.2](02-pods-replicasets-deployments.md) |
| **Deployment** | Rolling updates and rollbacks of ReplicaSets | Template change = rollout; selector is immutable | [9.3.2](02-pods-replicasets-deployments.md) |
| **Service** | Stable name and virtual IP for ready pods | Only *ready* pods; check endpoints first | [9.3.3](03-services-dns-ingress.md) |
| **Ingress / Gateway** | Host and path routing from outside | Needs a controller; Gateway API is the direction | [9.3.3](03-services-dns-ingress.md) |
| **ConfigMap / Secret** | Settings / credentials | Env vars need a restart; a Secret is base64, not encryption | [9.3.4](04-configuration-and-secrets.md) |
| **PVC / PV / StorageClass** | Storage requested, provided, and how | The claim's lifetime decides the data's | [9.3.7](07-storage-statefulsets.md) |
| **StatefulSet** | Stable names, DNS and per-pod volumes | Scaling down or deleting keeps the claims | [9.3.7](07-storage-statefulsets.md) |
| **HPA** | Scales replicas on metrics | `ceil(current × measured / target)`, % of the *request* | [9.3.6](06-resources-scheduling-autoscaling.md) |
| **PodDisruptionBudget** | Limits voluntary disruption | Applies to drains, not to deletes or crashes | [9.3.10](10-troubleshooting-day-two.md) |
| **Role / RoleBinding, ServiceAccount** | Who may do what; a pod's identity | Additive only; default account has no rights | [9.3.9](09-kubernetes-security.md) |
| **NetworkPolicy** | Allowed traffic between pods | Only with a plugin that enforces it | [9.3.9](09-kubernetes-security.md) |
| **Namespace, LimitRange, ResourceQuota** | Partition, defaults, caps | A namespace alone is not a security boundary | [9.3.6](06-resources-scheduling-autoscaling.md) |

## Numbers and defaults to remember

| Topic | Value |
|---|---|
| Probe defaults | period 10 s, timeout 1 s, failure threshold 3, success 1, initial delay 0 |
| Probe roles | startup holds off the others; readiness controls traffic (no restart); liveness restarts |
| Termination | `preStop` and endpoint removal start together; SIGTERM; grace period **30 s**; then SIGKILL |
| Rolling update | `maxSurge` and `maxUnavailable` (observed: `1` and `0`); progress deadline default **600 s** |
| Measured release (demo, about 400 requests) | no probes: 11 to 12 percent failed; readiness: 1 to 2 percent; readiness plus 5 s `preStop`: none |
| Exit codes | 137 = SIGKILL / OOM, 143 = SIGTERM |
| QoS classes | none = BestEffort; requests ≠ limits = Burstable; requests = limits = Guaranteed |
| CPU limit / memory limit | CPU is throttled (latency); memory is killed (`OOMKilled`) |
| HPA | sync **15 s**, tolerance 10 percent, scale-up fast (4 pods or 100 percent per 15 s), scale-down window **300 s** default |
| Service types | ClusterIP (inside), NodePort (30000-32767), LoadBalancer, headless (`clusterIP: None`) |
| DNS | `SERVICE.NAMESPACE.svc.cluster.local`; `ndots:5` |
| Reclaim policy | `Delete` removes the data with the claim |
| Version skew | kubectl ±1 minor; kubelet up to 3 minors older than the API server; upgrade one minor at a time |

## The commands

| Goal | Command |
|---|---|
| Which cluster and namespace | `kubectl config current-context`; always add `--context` and `-n` in scripts |
| Apply / render / diff | `kubectl apply -f`, `kubectl kustomize DIR`, `helm template`, `kubectl diff -f` |
| State of things | `kubectl get pods -o wide`, `get events --sort-by=.metadata.creationTimestamp`, `get endpointslices -l kubernetes.io/service-name=NAME` |
| Why | `kubectl describe pod NAME`, `kubectl logs NAME [--previous] [-c CONTAINER]` |
| Inside | `kubectl exec -it NAME -- sh`, `kubectl port-forward svc/NAME 8080:80`, `kubectl debug NAME --image=busybox --target=CONTAINER` |
| Rollouts | `kubectl rollout status|history|undo|restart deployment/NAME` |
| Permissions | `kubectl auth can-i VERB RESOURCE --as=system:serviceaccount:NS:SA` |
| Usage | `kubectl top pods`, `kubectl top node` |
| Helm | `helm install|upgrade|history|rollback|uninstall RELEASE CHART` |

## The troubleshooting ladder

1. **What changed?** (`rollout history`, Git, pipeline.) If a release: **roll back first**.
2. **`get`** the pods, nodes and endpoints.
3. **`describe` and events:** the reason is usually here.
4. **`logs`** (and `--previous`).
5. **Inside:** `exec`, `port-forward`, ephemeral container.
6. Fix through Git.

| Status | First place to look |
|---|---|
| `ImagePullBackOff` | Pod events: image name, tag, pull access |
| Crash loop / `Error` | `logs --previous`, exit code |
| `OOMKilled` | Memory limit, JVM heap setting |
| `Pending` | `FailedScheduling` event: requests, selector, volume |
| `CreateContainerConfigError` | Missing ConfigMap, Secret or key |
| `Running` but `0/1` | `Unhealthy` events: readiness path and port |
| Connection refused to a Service | Endpoints: selector and `targetPort` |
| `403 Forbidden` | RBAC: `auth can-i` |

## A review checklist for a workload

1. Deployment with at least two replicas, specific image tag, spread across nodes and zones.
2. Readiness probe (light and honest); startup probe if slow; liveness only if needed and dependency-free.
3. `preStop` pause, SIGTERM handled, grace period longer than the slowest request.
4. Requests from measured usage, a memory limit with headroom over the JVM heap, a deliberate CPU-limit policy.
5. HPA on the real bottleneck, a `minReplicas` that survives a node loss, a PodDisruptionBudget.
6. Config in ConfigMaps, secrets from an external store, mounted as files; rollouts triggered by config changes.
7. Own ServiceAccount, no token if the app does not call the API, minimal namespaced Role.
8. `restricted` Pod Security: non-root, no privilege escalation, all capabilities dropped, seccomp, read-only root filesystem.
9. Default-deny NetworkPolicy plus the needed allow rules, tested.
10. Manifests in Git (Kustomize overlays or a Helm chart), rendered and diffed in CI; backups of data, with tested restores.

## Interview questions

- **What is Kubernetes?** A system that keeps declared desired state true across a cluster of machines using controllers ([9.3.1](01-kubernetes-overview.md)).
- **Pod, ReplicaSet, Deployment?** Run it; keep the count; manage change and rollback ([9.3.2](02-pods-replicasets-deployments.md)).
- **How do pods find each other?** A Service gives a stable name and IP in front of ready pods; DNS resolves it; Ingress routes from outside ([9.3.3](03-services-dns-ingress.md)).
- **Are Secrets secure?** Base64 and unencrypted in etcd by default; protect with RBAC, encryption at rest and an external store ([9.3.4](04-configuration-and-secrets.md)).
- **How do you get zero-downtime deploys?** Readiness probes, `maxUnavailable: 0`, a `preStop` pause, SIGTERM handling and a sufficient grace period; prove it under load ([9.3.5](05-probes-rolling-updates-graceful-shutdown.md)).
- **Requests versus limits?** Requests reserve and drive scheduling and HPA percentages; limits cap (throttle CPU, kill on memory) ([9.3.6](06-resources-scheduling-autoscaling.md)).
- **Can you run a database on Kubernetes?** Yes with StatefulSets, replicated storage, an operator and tested backups; many teams use a managed database ([9.3.7](07-storage-statefulsets.md)).
- **Helm or Kustomize?** Helm for packages and third-party charts with release history; Kustomize for plain-YAML overlays; both rendered and reviewed in CI ([9.3.8](08-kustomize-and-helm.md)).
- **How do you secure a cluster?** Least-privilege RBAC and service accounts, `restricted` Pod Security, network policies, scoped deployment rights, external secrets ([9.3.9](09-kubernetes-security.md)).
- **A pod is failing: what do you do?** What changed, `get`, events and `describe`, `logs`, then inside; roll back first if it was a release ([9.3.10](10-troubleshooting-day-two.md)).

Back to [the Kubernetes series overview](01-kubernetes-overview.md) · [the area overview](../01-cloud-and-containers-overview.md) · [the index](../../README.md) · [Roadmap](../../roadmap.md).

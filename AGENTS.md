# AGENTS.md

## Purpose

- Scaffold for a standalone Project Continuum feature worker: example nodes plus the worker, CI/CD, Helm chart and local stack needed to ship them.

## Ownership

- `features/continuum-feature-hello/`: Kotlin example feature module (`org.projectcontinuum.feature`)
- `features/continuum-feature-hello-java/`: Java example feature module (`org.projectcontinuum.feature-java`)
- `worker/`: Spring Boot worker bundling the feature modules (`org.projectcontinuum.worker`); Jib image `projectcontinuum/continuum-feature-template`
- `charts/continuum-feature-template/`: Helm chart for the worker
- `docker/`: local Continuum stack for development
- `.github/workflows/`: CI, version validation, Maven Central / Docker Hub / Helm releases

## Local Contracts

- `gradle.properties` owns `continuumPlatformVersion` and `featureVersion`; build files read them via `property(...)`
- Changes under `features/` or `worker/` require a `featureVersion` bump; chart `appVersion` must equal `featureVersion`
- Node rules (annotation, metadata id, schemas, row numbering, Jackson 3) are listed in [CLAUDE.md](CLAUDE.md)
- Worker task queue `CONTINUUM_FEATURE_TEMPLATE_TASK_QUEUE` must stay unique across workers; keep `application.yaml` and chart `values.yaml` in sync

## Work Guidance

- Keep the Kotlin and Java example nodes behaviourally identical
- Keep README.md version badges and tables in sync with `gradle.properties` and the Gradle plugin defaults

## Verification

- `./gradlew build` (compiles, runs unit tests, runs `validateContinuumNodeAnnotations`)

## Child DOX Index

- None

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
- Node rules (annotation, metadata id, schemas, row numbering, Jackson 3) are listed under Node Contract below
- Worker task queue `CONTINUUM_FEATURE_TEMPLATE_TASK_QUEUE` must stay unique across workers; keep `application.yaml` and chart `values.yaml` in sync

## Work Guidance

- Keep the Kotlin and Java example nodes behaviourally identical
- Keep README.md version badges and tables in sync with `gradle.properties` and the Gradle plugin defaults

## Verification

- `./gradlew build` (compiles, runs unit tests, runs `validateContinuumNodeAnnotations`)

## Commands

```bash
./gradlew build                     # compile, test, validate @ContinuumNode annotations, package
./gradlew test                      # all tests
./gradlew :features:continuum-feature-hello:test --tests "*HelloWorldNodeModelTest"
cd docker && docker compose up -d   # local Continuum stack (Workbench on :3002)
./gradlew :worker:bootRun           # run the worker on :8090 against the local stack
```

Prerequisites: JDK 21, Docker. All dependencies resolve from Maven Central; no tokens needed.

## Versions

- `gradle.properties` is the single source of truth: `continuumPlatformVersion` (platform/plugins, currently 0.0.20) and `featureVersion` (this repo's modules, worker image and chart `appVersion`).
- Module `build.gradle.kts` files must read both via `property(...)`; never hardcode. The plugin's built-in `continuumVersion` default is stale.
- Bump `featureVersion` whenever `features/` or `worker/` change. CI (`validate-versions.yml`) enforces it and requires `charts/*/Chart.yaml` `appVersion` to match.

## Node Contract

- Annotate with `@ContinuumNode`, extend `ProcessNodeModel` (or `TriggerNodeModel`).
- `metadata.id` and `metadata.nodeModel` = `this.javaClass.name`.
- Provide `propertiesSchema` (JSON Schema) and `propertiesUISchema` (JSON Forms); properties are validated against the schema before `execute()`.
- Read with `inputs["port"].use { it.read() }` (null = end of stream); write with `createOutputPortWriter("port").use { it.write(rowNumber, row) }`. Row numbers are `Long` starting at 0.
- Cell values: String, Int, Long, Float, Double, Boolean, List, Map, SVG. Never null.
- Errors: `NodeRuntimeException(isRetriable = …, workflowId, nodeId, message)`.
- Node docs: `src/main/resources/<package path>/<ClassName>.doc.md`.
- Each feature module needs `AutoConfigure` (`@ComponentScan`) listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

## Gotchas

- Jackson 3: import `tools.jackson.*`, not `com.fasterxml.jackson.*`. Its exceptions are unchecked.
- The worker task queue (`CONTINUUM_NODE_TASK_QUEUE`) must be unique per worker.
- Port 8080 belongs to the api-server locally; the worker uses 8090.
- Java `NodeData` has no default arguments; pass all 15 constructor parameters in order.

## Child DOX Index

- None

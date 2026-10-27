# CLAUDE.md

Guidance for Claude Code (and other coding agents) working in this repository. See also [AGENTS.md](AGENTS.md).

## This Repo

Scaffold for a standalone Continuum feature worker. Two example nodes (Kotlin `HelloWorldNodeModel`, Java `HelloJavaNodeModel`) are bundled by the Spring Boot app in `worker/`, which registers them with Temporal and the Continuum api-server.

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

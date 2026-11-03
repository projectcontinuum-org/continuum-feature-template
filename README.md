<div align="center">
  <h1>Continuum Feature Template</h1>
  <strong>The starting point for building your own nodes on <a href="https://projectcontinuum.org">Project Continuum</a></strong><br/>
  Clone it, rename it, write a node — ship a worker.
</div>

<br/>

<div align="center">
  <a href="https://github.com/projectcontinuum-org/continuum-feature-template/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/projectcontinuum-org/continuum-feature-template/ci.yml?branch=main&label=build&logo=github" alt="Build"></a>
  <a href="https://central.sonatype.com/namespace/org.projectcontinuum.core"><img src="https://img.shields.io/badge/Continuum_Platform-0.0.20-0F766E" alt="Continuum Platform 0.0.20"></a>
  <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.2">
  <img src="https://img.shields.io/badge/Java-supported-ED8B00?logo=openjdk&logoColor=white" alt="Java supported">
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.6">
  <img src="https://img.shields.io/badge/JDK-21-red?logo=openjdk&logoColor=white" alt="JDK 21">
  <img src="https://img.shields.io/badge/Gradle-8.14-02303A?logo=gradle&logoColor=white" alt="Gradle 8.14">
  <img src="https://img.shields.io/badge/Temporal-1.28-000000?logo=temporal&logoColor=white" alt="Temporal 1.28">
  <img src="https://img.shields.io/badge/Nodes-2_examples-10B981" alt="2 example nodes">
  <a href="https://docs.projectcontinuum.org/docs/contributing/create-new-nodes"><img src="https://img.shields.io/badge/docs-Create_New_Nodes-3B82F6?logo=readthedocs&logoColor=white" alt="Docs"></a>
</div>

---

## 🌐 Part of Project Continuum

[Project Continuum](https://projectcontinuum.org) is a distributed, crash-proof workflow platform: you build workflows visually in the browser-based **Workbench**, and **feature workers** execute each node as a durable [Temporal](https://temporal.io) activity.

Continuum is extended by **adding new workers, not by modifying existing ones**. This repository is the scaffold for such a worker. It already includes the Gradle plugins, the Spring Boot worker, Temporal registration, CI/CD, a Helm chart and a local stack, so you only need to write nodes.

---

## 🔥 What Is This

A standalone, ready-to-build Gradle project with:

- **Two example nodes**: the same *Hello World* node written once in **Kotlin** and once in **Java**, with unit tests and node documentation.
- **A Spring Boot worker** that bundles both modules and registers every `@ContinuumNode` with Temporal and the api-server at startup.
- **The Continuum Gradle plugins** from Maven Central. They bring in the platform BOMs, `continuum-commons`, test libraries, publishing and a build-time check that every node is annotated correctly.
- **CI/CD**: build on every PR, publish libraries to Maven Central and the worker image to Docker Hub on release, and ship a Helm chart.
- **A local stack** (`docker/`) with Temporal, Kafka, MinIO, the Continuum platform services and the Workbench, so you can drag your node onto a canvas and run it.

---

## 🧩 Included Nodes

| Node | Language | Module | Category |
|------|----------|--------|----------|
| **Hello World** | Kotlin | `features/continuum-feature-hello` | Examples |
| **Hello Java** | Java | `features/continuum-feature-hello-java` | Examples |

Both nodes read a table, append a greeting column built from a configurable source column, and write the result:

| Port | Direction | Description |
|------|-----------|-------------|
| `input` | in | Any table |
| `output` | out | Input columns + the greeting column |

| Property | Default | Description |
|----------|---------|-------------|
| `nameColumn` | `name` | Column to read the name from (falls back to `World` if missing) |
| `greeting` | `Hello` | Greeting prefix |
| `outputColumn` | `greeting` | Name of the new column |

**Example:** `{ name: "Alice", age: 30 }` → `{ name: "Alice", age: 30, greeting: "Hello, Alice!" }`

---

## 📦 Tech Stack

| Component | Version | Provided by |
|-----------|---------|-------------|
| Continuum platform (`continuum-commons`, worker starter) | `0.0.20` | `continuumPlatformVersion` in `gradle.properties` |
| Kotlin | 2.2 | `org.projectcontinuum.feature` plugin |
| Java toolchain | 21 | Gradle plugin |
| Spring Boot / Spring Cloud | 4.0.6 / 2025.1.1 | Gradle plugin (BOM) |
| Temporal SDK | 1.28.0 | Gradle plugin (BOM) |
| Jackson | 3.x (`tools.jackson.*`) | Gradle plugin |
| Tests | JUnit 5 + Mockito / mockito-kotlin | Gradle plugin |
| Container image | Jib on `eclipse-temurin:21-jre` | `org.projectcontinuum.worker` plugin |

All Continuum artifacts and Gradle plugins are on **Maven Central**, so you don't need a GitHub token or a private registry.

> [!NOTE]
> Jackson 3 lives under `tools.jackson.*`, not `com.fasterxml.jackson.*`. Code copied from older Continuum nodes needs its imports updated.

---

## 🚀 Quick Start

### Prerequisites

| Requirement | Version | Check |
|-------------|---------|-------|
| JDK | 21+ | `java -version` |
| Docker + Compose | 20.10+ / v2 | `docker compose version` |
| RAM for containers | ~8 GB | — |

Gradle is provided by the wrapper (`./gradlew`).

### 1. Build and test

```bash
./gradlew build
```

This compiles both feature modules, runs their unit tests, validates the `@ContinuumNode` annotations and packages the worker jar.

### 2. Start the local Continuum stack

```bash
cd docker
docker compose up -d
```

Wait until `docker compose ps` shows the services healthy. The `temporal-search-attributes-init` container exits once setup is done; that's expected.

### 3. Run your worker

```bash
./gradlew :worker:bootRun
```

The worker connects to Temporal on `localhost:7233`, registers its nodes with the api-server, and serves on port `8090`.

### 4. Use your nodes

Open the **Workbench** at <http://localhost:3002>. *Hello World* and *Hello Java* appear in the node palette under **Examples**. Connect one to a table source (for example *Create Table* from `continuum-feature-base`, which also runs in the stack) and run the workflow.

### Local endpoints

| Service | URL |
|---------|-----|
| Continuum Workbench | <http://localhost:3002> |
| Continuum API server | <http://localhost:8080> |
| Credentials manager | <http://localhost:3002/credentials-manager/ui/> |
| Your worker (actuator) | <http://localhost:8090/actuator/health> |
| Temporal UI | <http://localhost:38081> |
| Kafka UI | <http://localhost:38082> |
| MinIO console | <http://localhost:39001> (`minioadmin` / `minioadmin`) |

---

## 🛠️ Make It Yours

1. **Rename the project.**
   - Change `rootProject.name` in `settings.gradle.kts`.
   - Change `repoName` in `gradle.properties`.
   - Change `spring.application.name` in `worker/src/main/resources/application.yaml`.
   - Change `jibToImage` in `worker/build.gradle.kts`.
   - Rename the chart folder and its references in `charts/`.
2. **Pick a unique task queue.** Set `continuum.core.worker.node-task-queue` in `application.yaml` (default `CONTINUUM_FEATURE_TEMPLATE_TASK_QUEUE`) and `taskQueue` in the chart's `values.yaml`.
3. **Rename the packages** (`org.projectcontinuum.feature.example` → your own) and update each module's `group`, `AutoConfigure` and `AutoConfiguration.imports`.
4. **Write your nodes**, using the examples as a guide (see below).
5. **Delete what you don't need.** If you only write Kotlin, remove `continuum-feature-hello-java` and the `feature-java` plugin line.

> [!IMPORTANT]
> Every worker **must** use its own task queue. If two workers share a queue, Temporal hands a node's activity to whichever worker polls first, including one that doesn't have the node.

### Adding a node

```kotlin
@ContinuumNode                                     // prototype-scoped Spring bean, required
class MyNodeModel(
    private val objectMapper: ObjectMapper         // tools.jackson.databind.ObjectMapper
) : ProcessNodeModel() {

    final override val inputPorts  = mapOf("input"  to ContinuumWorkflowModel.NodePort("Input Table", APPLICATION_OCTET_STREAM_VALUE))
    final override val outputPorts = mapOf("output" to ContinuumWorkflowModel.NodePort("Output Table", APPLICATION_OCTET_STREAM_VALUE))
    override val categories = listOf("My Category")

    override val metadata = ContinuumWorkflowModel.NodeData(
        id = this.javaClass.name,                  // must be the class name
        nodeModel = this.javaClass.name,           // must be the class name
        title = "My Node",
        description = "What it does",
        inputs = inputPorts,
        outputs = outputPorts,
        propertiesSchema = /* JSON Schema */,
        propertiesUISchema = /* JSON Forms UI schema */
    )

    override fun execute(properties: Map<String, Any>?, inputs: Map<String, NodeInputReader>,
                         nodeOutputWriter: NodeOutputWriter, nodeProgressCallback: NodeProgressCallback) {
        nodeOutputWriter.createOutputPortWriter("output").use { writer ->
            inputs.getValue("input").use { reader ->
                var rowNumber = 0L                 // row numbers start at 0
                while (true) {
                    val row = reader.read() ?: break
                    writer.write(rowNumber++, row + ("new" to "value"))
                }
            }
        }
    }
}
```

Then add `src/main/resources/<package path>/MyNodeModel.doc.md`. The Workbench shows it as the node's help page.

**Key rules:**

- Properties are validated against `propertiesSchema` **before** `execute()` runs.
- Cell values must be `String`, `Int`, `Long`, `Float`, `Double`, `Boolean`, `List`, `Map` or an SVG document, and never `null`.
- Throw `NodeRuntimeException(workflowId, nodeId, message, isRetriable = false)` for errors that a retry won't fix.

📖 The step-by-step guide is **[Create New Nodes](https://docs.projectcontinuum.org/docs/contributing/create-new-nodes)**.

---

## ⚙️ Configuration

The worker reads these environment variables. The defaults point at the local stack in `docker/`.

| Variable | Default | Purpose |
|----------|---------|---------|
| `CONTINUUM_NODE_TASK_QUEUE` | `CONTINUUM_FEATURE_TEMPLATE_TASK_QUEUE` | Temporal task queue for this worker's nodes |
| `TEMPORAL_SERVER_ADDRESS` | `localhost:7233` | Temporal frontend |
| `TEMPORAL_NAMESPACE` | `default` | Temporal namespace |
| `CONTINUUM_API_SERVER_BASE_URL` | `http://localhost:8080` | Where nodes are registered |
| `CONTINUUM_CREDENTIALS_SERVER_BASE_URL` | `http://localhost:8083` | Where nodes resolve stored credentials |
| `CONTINUUM_KAFKA_MAIN_BROKER_LIST` | `localhost:39092,…39094` | Kafka brokers (feature registration events) |
| `CONTINUUM_KAFKA_MAIN_SCHEMA_REGISTRY` | `http://localhost:38080` | Schema registry |
| `CONTINUUM_MINIO_ENDPOINT` | `http://localhost:39000` | Object storage for node data |
| `CONTINUUM_MINIO_USERNAME` / `CONTINUUM_MINIO_PASSWORD` | `minioadmin` | MinIO credentials |
| `CONTINUUM_CACHE_STORAGE_PATH` | `./.continuum-cache/workflow-data` | Local scratch space |
| `SERVER_PORT` | `8090` | Worker HTTP port (8080 is taken by the api-server) |

---

## 📁 Project Structure

```
continuum-feature-template/
├── features/
│   ├── continuum-feature-hello/          # Kotlin feature module
│   │   └── src/
│   │       ├── main/kotlin/.../example/
│   │       │   ├── AutoConfigure.kt              # @ComponentScan for this module
│   │       │   └── node/HelloWorldNodeModel.kt   # the node
│   │       ├── main/resources/
│   │       │   ├── META-INF/spring/…AutoConfiguration.imports
│   │       │   └── …/node/HelloWorldNodeModel.doc.md
│   │       └── test/kotlin/…/HelloWorldNodeModelTest.kt
│   └── continuum-feature-hello-java/     # Java feature module (same layout)
├── worker/                               # Spring Boot worker bundling the features
│   ├── build.gradle.kts                  # depends on the feature modules; Jib image
│   └── src/main/resources/application.yaml
├── charts/continuum-feature-template/    # Helm chart for Kubernetes
├── docker/                               # Local Continuum stack (docker compose)
├── .github/workflows/                    # CI, release, chart publishing
├── build.gradle.kts
├── settings.gradle.kts                   # plugin versions + module list
└── gradle.properties                     # continuumPlatformVersion, featureVersion
```

---

## 🧪 Testing

```bash
./gradlew test                                              # all modules
./gradlew :features:continuum-feature-hello:test            # one module
./gradlew :features:continuum-feature-hello:test --tests "*HelloWorldNodeModelTest"
```

Nodes are plain classes, so you can unit-test them by mocking `NodeInputReader`, `NodeOutputWriter` and `NodeProgressCallback`. See the bundled tests for the pattern. Kotlin nodes that inject an `ObjectMapper` can take `jacksonObjectMapper()` in tests.

---

## 🚢 Release & Deployment

Versions are managed in `gradle.properties`:

| Property | Meaning |
|----------|---------|
| `continuumPlatformVersion` | Continuum platform the modules compile against |
| `featureVersion` | Version of your feature modules, worker image and Helm `appVersion` |

| Workflow | Trigger | What it does |
|----------|---------|--------------|
| `ci.yml` | PR, push to `main` | `./gradlew build` |
| `validate-versions.yml` | PR | Fails if `features/` or `worker/` changed without a `featureVersion` bump, or if the chart `appVersion` ≠ `featureVersion` |
| `lint-test.yml` | PR touching `charts/` | `ct lint` |
| `build-module.yml` | Manual | Build one module and optionally publish |
| `on-release.yml` | GitHub release | Feature modules → Maven Central (JReleaser), worker → Docker Hub (Jib) |
| `release.yml` | GitHub release | Package the Helm chart and update the `gh-pages` Helm index |

The release workflows need the secrets `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD`, `GPG_KEY_ID`, `GPG_PASSPHRASE`, `GPG_SECRET_KEY`, `GPG_PUBLIC_KEY`, `DOCKER_HUB_USERNAME` and `DOCKER_HUB_PASSWORD`.

**Kubernetes:** the chart installs next to `continuum-infra` and `continuum-platform` in the same namespace:

```bash
helm install continuum-feature-template ./charts/continuum-feature-template -n continuum
```

See [Deployment](https://docs.projectcontinuum.org/docs/deployment/helm-charts) for the full setup.

---

## 🔗 Related Repositories

| Repository | Description |
|------------|-------------|
| [continuum-platform-core](https://github.com/projectcontinuum-org/continuum-platform-core) | Platform services, `continuum-commons`, worker starter and Gradle plugins |
| [continuum-workbench](https://github.com/projectcontinuum-org/continuum-workbench) | Browser-based workflow IDE |
| [continuum-local-setup](https://github.com/projectcontinuum-org/continuum-local-setup) | One-command local stack with the published feature workers |
| [continuum-feature-base](https://github.com/projectcontinuum-org/continuum-feature-base) | General-purpose analytics and data processing nodes |
| [continuum-feature-cheminformatics](https://github.com/projectcontinuum-org/continuum-feature-cheminformatics) | RDKit-powered cheminformatics nodes |
| [continuum-feature-ai](https://github.com/projectcontinuum-org/continuum-feature-ai) | AI/ML nodes (LLM fine-tuning) |
| [continuum-feature-knime](https://github.com/projectcontinuum-org/continuum-feature-knime) | Run KNIME workflows as Continuum nodes |
| [continuum-docs](https://github.com/projectcontinuum-org/continuum-docs) | Documentation site: [docs.projectcontinuum.org](https://docs.projectcontinuum.org) |

---

<div align="center">
  <sub>Built for <a href="https://projectcontinuum.org">Project Continuum</a> · Questions and bugs → <a href="https://github.com/projectcontinuum-org/continuum-platform-core/issues">issue tracker</a></sub>
</div>

## 📄 License

[Apache 2.0](LICENSE)

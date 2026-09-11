# Architecture

This is a Kotlin/Ktor service template built to be productive **the second you open it** in
VS Code: hot reload, an always-attached debugger, black-box tests with a watchable browser,
and a devcontainer that provisions all of it. This document maps every moving part and how
they talk to each other.

## Component inventory

| Component                       | File(s)                                                                                           | Responsibility                                                                                                                                                                                                                    |
| ------------------------------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Devcontainer**                | `.devcontainer/devcontainer.json`, `post-create.sh`                                               | Defines the container image, features, mounts, ports; runs one-time setup                                                                                                                                                         |
| **`desktop-lite` feature**      | `devcontainer.json` → `features`                                                                  | Fluxbox + TigerVNC + noVNC, gives the container a real `DISPLAY` and a browser-viewable desktop on `:6080`                                                                                                                        |
| **Kotlin LSP**                  | `JetBrains.kotlin-server` extension                                                               | Language server: completion, diagnostics, formatting, refactoring. Imports the project via its own Gradle sync                                                                                                                    |
| **Gradle**                      | `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`       | Build tool: compiles Kotlin, runs tests, packages the app, drives Kover/ktlint                                                                                                                                                    |
| **Ktor application**            | `src/main/kotlin/com/example/domain/*`, `application/*`, `adapter/http/*`, `src/main/resources/*` | The actual service, split hexagonally: `domain` (value objects), `application` (use cases), `adapter.http` (Ktor routing/plugins). Both HTTP modules are declared by name in `application.yaml`, proving that wiring path is real |
| **VS Code tasks**               | `.vscode/tasks.json`                                                                              | Wraps Gradle invocations as named, chainable, backgroundable commands                                                                                                                                                             |
| **VS Code launch configs**      | `.vscode/launch.json`                                                                             | Attaches the JVM debugger to a task's JDWP port instead of launching the JVM itself                                                                                                                                               |
| **unit / arch / browser tests** | `src/test/kotlin/com/example/unit/*`, `.../arch/*`, `.../browser/*`                               | In-process and Konsist architecture tests run with `test`; Playwright HTTP and browser scenarios run with `browserTest`                                                                                                           |
| **Pre-commit hook**             | `.githooks/pre-commit`                                                                            | Auto-formats staged Kotlin with `formatKotlin`, then blocks commits that still fail `lintKotlin`                                                                                                                                  |
| **CI**                          | `.github/workflows/ci.yml`                                                                        | Same checks as local, plus a packaged-jar smoke test, on every push/PR                                                                                                                                                            |

## Component map

```mermaid
flowchart TB
    subgraph Host["Host machine"]
        VSCode["VS Code (client)"]
    end

    subgraph Container["Devcontainer (mcr.microsoft.com/devcontainers/java:25-bookworm)"]
        KotlinLSP["Kotlin LSP\n(JetBrains.kotlin-server)"]
        GradleDaemonDefault["Gradle daemon\n(default cache dir)\nused by Kotlin LSP import"]
        GradleWatch["Gradle daemon\n(.gradle/watch)\n-t classes"]
        GradleRun["Gradle daemon\n(.gradle/run)\nrun -Pdev"]
        AppJVM["Ktor app JVM\n:8080  JDWP :5005"]
        TestJVM["Test JVM\nJDWP :5007 (on demand)"]
        Fluxbox["Fluxbox + TigerVNC (:1)"]
        noVNC["noVNC (:6080)"]
        Chromium["Chromium (Playwright)"]
        GradleCache[("~/.gradle volume")]
        PlaywrightCache[("~/.cache/ms-playwright volume")]
    end

    subgraph GH["GitHub"]
        Actions["GitHub Actions (ci.yml)"]
    end

    VSCode -- "F5 / tasks / debug attach" --> GradleWatch
    VSCode -- "F5 / tasks / debug attach" --> GradleRun
    VSCode -- "project import" --> KotlinLSP
    KotlinLSP --> GradleDaemonDefault
    GradleWatch -->|recompiles| AppJVM
    GradleRun --> AppJVM
    VSCode -- "attach :5005 / :5007" --> AppJVM
    VSCode -- "attach :5005 / :5007" --> TestJVM
    TestJVM -- "drives" --> Chromium
    Chromium -- "renders on" --> Fluxbox
    Fluxbox --> noVNC
    VSCode -. "watch live" .-> noVNC
    TestJVM -- "HTTP" --> AppJVM
    GradleWatch --- GradleCache
    GradleRun --- GradleCache
    GradleDaemonDefault --- GradleCache
    TestJVM --- PlaywrightCache
    Actions -- "same Gradle tasks, no VS Code" --> Actions
```

## Code layering: hexagonal architecture + DDD

`src/main/kotlin/com/example` is split into three packages, each only allowed to depend
inward, enforced by `src/test/kotlin/com/example/arch/HexagonalArchitectureTest.kt` (Konsist)
on every `test` run rather than left as unchecked convention:

```mermaid
flowchart LR
    Adapter["adapter.http\n(Ktor routes, plugins, wire DTOs)"] --> Application["application\n(use cases: GreetingService, ServiceInfoService)"]
    Application --> Domain["domain\n(value objects: Greeting, ServiceInfo)"]
```

- **`domain`** — plain data classes with zero framework imports (no `@Serializable`, no Ktor).
  This is the DDD tactical layer: today just small value objects, but it's where
  entities/aggregates/domain events would live if the template grew real business rules.
- **`application`** — use case classes that orchestrate the domain. Also framework-free; an
  adapter (HTTP today, a CLI or message consumer tomorrow) calls into these, never the other
  way round.
- **`adapter.http`** — the only package allowed to import Ktor. Routes call an application
  service and map its domain return value to a `@Serializable` response DTO, so the domain
  model never leaks into the wire format (and vice versa).
- There are no outbound adapters yet (no persistence — see "No database, no Testcontainers"
  below). When one is needed, add `adapter.<technology>` alongside `adapter.http` and, if the
  application layer needs to call it, a port interface in `application` that the new adapter
  implements.

## Use case: opening the devcontainer for the first time

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant VSC as VS Code
    participant Docker
    participant PC as post-create.sh
    participant Gradle

    Dev->>VSC: "Reopen in Container"
    VSC->>Docker: pull/build image + apply features (desktop-lite)
    Docker->>Docker: mount named volumes (.gradle, ms-playwright)\nnote: parent dirs created as root
    Docker-->>VSC: container running
    VSC->>PC: run postCreateCommand
    PC->>PC: sudo chown -R vscode:vscode /home/vscode
    PC->>PC: git config core.hooksPath .githooks
    PC->>Gradle: classes testClasses (default cache dir)
    PC->>Gradle: classes (--project-cache-dir=.gradle/watch)
    PC->>Gradle: classes (--project-cache-dir=.gradle/run)
    PC->>Gradle: playwrightInstall (Chromium + apt deps, --with-deps)
    PC-->>VSC: postCreateCommand done
    VSC->>VSC: install extensions (Kotlin LSP, Prettier, ...)
    VSC->>Gradle: Kotlin LSP imports project (its own daemon)
    VSC-->>Dev: workspace ready
```

The whole-home `chown` exists because Docker creates the _parent_ of a volume mount
(`~/.cache`) as root the first time, which silently broke Kotlin LSP's own cache the first
time this template added the Playwright volume — see `AGENTS.md` / commit history for that
regression. Chowning everything instead of enumerating mount points avoids repeating it.

## Use case: F5 — hot reload with an always-attached debugger

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant VSC as VS Code (launch.json)
    participant Watch as Gradle (.gradle/watch)
    participant Run as Gradle (.gradle/run)
    participant App as App JVM (Netty, :8080)

    Dev->>VSC: Press F5 ("Debug Ktor (hot reload)")
    VSC->>Watch: preLaunchTask "dev" → gradlew -t classes
    VSC->>Run: preLaunchTask "dev" → gradlew run -Pdev
    Run->>App: start with -Dio.ktor.development=true\n-agentlib:jdwp=...address=127.0.0.1:5005
    App->>App: watches build/classes, build/resources
    VSC->>App: attach debugger to 127.0.0.1:5005
    Note over VSC,App: Attach, not launch — debugger survives every reload below.
    Dev->>Dev: edits Application.kt
    Watch->>Watch: detects change, recompiles to build/classes
    App->>App: detects updated classes, reloads classloader
    Dev->>App: GET http://localhost:8080/
    App-->>Dev: 200 OK (new code), breakpoints still active
```

## Use case: debugging a single test

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant VSC as VS Code (launch.json)
    participant Gradle
    participant TestJVM as Test JVM (JDWP :5007)

    Dev->>VSC: open *Test.kt, run "Debug tests"
    VSC->>Gradle: preLaunchTask "test: debug"\ngradlew --debug-jvm test --tests "*.${fileBasenameNoExtension}"
    Gradle->>TestJVM: fork, debugOptions.port=5007, suspend
    TestJVM-->>Gradle: "Listening for transport dt_socket" (background pattern match)
    VSC->>TestJVM: attach debugger to 127.0.0.1:5007
    TestJVM->>TestJVM: resume, run test, hit breakpoint
    Dev->>TestJVM: step through in debugger
```

Port 5007 is deliberate: Gradle's own `--debug-jvm` defaults to 5005, which would collide
with the app JVM's debug port from the previous use case.

## Use case: black-box (e2e) tests + the live browser view

Playwright HTTP and browser e2e tests run separately with `browserTest`.
target either the already-running F5 dev server or a server started in the test JVM, decided by
`BASE_URL`.

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant Gradle
    participant TestJVM as Test JVM
    participant Embedded as Embedded Netty (port 0)
    participant PW as Playwright
    participant Chrome as Chromium
    participant Desk as Fluxbox/TigerVNC (:1)
    participant VNC as noVNC (:6080)
    participant DevServer as F5 dev server (:8080, if running)

    Dev->>Gradle: ./gradlew browserTest
    Gradle->>TestJVM: fork (test source set, com/example/browser/** only)

    alt BASE_URL is set
        TestJVM->>DevServer: HTTP requests go here instead
        Note over TestJVM,DevServer: App breakpoints on :5005 still work
    else BASE_URL unset (default)
        TestJVM->>Embedded: embeddedServer(Netty, port=0){ module() }.start()
        Embedded-->>TestJVM: resolvedConnectors().first().port
        Note over TestJVM,Embedded: Same JVM as the test -\ntest and app breakpoints share one session
    end

    TestJVM->>PW: Playwright.create()
    PW->>Chrome: chromium().launch(headless = DISPLAY unset)

    alt DISPLAY set (devcontainer desktop)
        Chrome->>Desk: render window on :1
        Desk->>VNC: stream framebuffer
        Dev-->>VNC: open localhost:6080 and watch live
    else no DISPLAY (CI)
        Chrome->>Chrome: render headless, full speed
    end

    Chrome->>Embedded: GET /swagger, GET /swagger/documentation.yaml
    Chrome-->>TestJVM: page loaded, ".swagger-ui" present
    TestJVM->>TestJVM: assertTrue(...)
    TestJVM->>Embedded: shutdown hook stops the embedded server (if used)
```

If a headed launch fails (stale `DISPLAY`, desktop not up yet), `SwaggerUiBrowserTest` catches
it and retries headless rather than failing the whole test.

## Use case: CI pipeline

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant GH as GitHub
    participant CI as GitHub Actions runner
    participant Gradle
    participant Jar as Fat jar (java -jar)

    Dev->>GH: push / open PR
    GH->>CI: trigger ci.yml
    CI->>CI: checkout, setup-java 25, setup-gradle (validates wrapper)
    CI->>CI: restore ~/.cache/ms-playwright (actions/cache)
    CI->>Gradle: build koverHtmlReport (compile, lintKotlin, unit tests, coverage)
    CI->>Gradle: buildFatJar
    CI->>Jar: java -jar ktor-template-all.jar &
    loop up to 20s
        CI->>Jar: curl /health/live
    end
    Jar-->>CI: 200 OK → kill jar, continue
    CI->>Gradle: playwrightInstall browserTest (headless, no DISPLAY)
    alt any step failed
        CI->>GH: upload test-reports artifact
    end
    CI->>GH: upload coverage-report artifact (always)
```

## Use case: committing

```mermaid
sequenceDiagram
    actor Dev as Developer
    participant Git
    participant Hook as .githooks/pre-commit
    participant Gradle

    Dev->>Git: git commit
    Git->>Hook: core.hooksPath → run pre-commit
    Hook->>Gradle: gradlew --quiet formatKotlin
    Gradle-->>Hook: staged files reformatted on disk
    Hook->>Git: git add (re-stage originally staged files)
    Hook->>Gradle: gradlew --quiet lintKotlin
    alt lint still fails
        Gradle-->>Hook: non-zero exit
        Hook-->>Dev: fix remaining issues manually + abort
    else lint passes
        Gradle-->>Hook: success
        Hook-->>Git: exit 0
        Git->>Git: commit created (with auto-formatted changes)
    end
```

`core.hooksPath` is set once by `post-create.sh`, so this works the same for anyone who
opens the devcontainer — no separate hook manager dependency.

## Key design decisions (see `AGENTS.md` for the full list)

- **Attach, don't launch**: both debugger configs attach to a JDWP port opened by a Gradle
  task, so the debugger session outlives hot reloads and test re-runs.
- **Three separate debug ports** (5005 app, 5007 tests) and **three Gradle cache dirs**
  (default, `.gradle/watch`, `.gradle/run`) exist purely to stop the dev loop, the test
  runner, and the Kotlin LSP's own project import from fighting each other over the same
  lock or port.
- **Browser tests are excluded by package, not by source set or `@Tag`** (`test` excludes
  `com/example/browser/**`, `browserTest` includes only it) — kept off `check`/`build` so a
  normal build never needs a browser.
- **No database, no Testcontainers**: this template intentionally stays infrastructure-free;
  add a `Fixtures` seam (see `AGENTS.md`) before adding either.
- **Hexagonal layering is enforced by a test, not just convention**: `HexagonalArchitectureTest`
  (Konsist) fails the build if `domain`/`application` ever import Ktor or an outer layer, so the
  boundary survives contributors who haven't read this document.

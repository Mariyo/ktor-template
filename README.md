# ktor-template-devcontainer

Minimal Ktor service template for VS Code devcontainers: instant startup, hot reload, debugger attachment, browser-based Swagger verification, and a ready-made test/lint pipeline.

| Component | Version       |
| --------- | ------------- |
| Kotlin    | 2.4.20        |
| Ktor      | 3.6.0 (Netty) |
| Gradle    | 9.8.0         |
| JDK       | 25            |

## What is included

- Devcontainer configured for Java 25 with the desktop-lite feature.
- F5-style local development loop: continuous compile + app reload + debugger attach.
- Ktor app with JSON, request IDs, request logging, response compression, Prometheus metrics, health checks, and Swagger UI.
- Hexagonal architecture (ports & adapters) with a DDD-style domain layer, enforced by Konsist arch tests.
- Unit tests, Playwright browser tests, and arch tests.
- Kover coverage, ktlint formatting, and a pre-commit quality gate.
- Plain and JSON logback profiles, and production-ready fat-jar packaging.

## Code architecture

The app code under `src/main/kotlin/com/example` is layered hexagonally. `src/test/kotlin/com/example/arch/HexagonalArchitectureTest.kt` uses [Konsist](https://konsist.lemonappdev.com/) to assert this dependency direction on every `./gradlew test` run, so a layering violation fails CI instead of only showing up in review. See [ARCHITECTURE.md](ARCHITECTURE.md) for the full component map.

## Quick start

1. Open the folder in VS Code and choose Reopen in Container.
2. Press F5.

This starts:

- a Gradle watcher that recompiles on save,
- the Ktor server on http://127.0.0.1:8080,
- a JVM debug session attached on 127.0.0.1:5005.

Use the dev: stop task to fully shut down the watch/server daemons and app JVM.

## Runtime features

The template app exposes these routes:

| Route                           | Description                                                         |
| ------------------------------- | ------------------------------------------------------------------- |
| GET /                           | JSON greeting                                                       |
| GET /info                       | Service name and version (wired via a second, YAML-declared module) |
| GET /health/live                | Liveness probe                                                      |
| GET /health/ready               | Readiness probe                                                     |
| GET /metrics                    | Prometheus scrape output                                            |
| GET /swagger                    | Swagger UI                                                          |
| GET /swagger/documentation.yaml | OpenAPI spec                                                        |

Errors are returned as JSON `{"error": ..., "status": ...}`. Domain rule violations (`DomainValidationException`), malformed requests and missing resources map to 400/404; anything unexpected is logged and returned as a generic 500 without its message.

## Devcontainer and hot reload

The devcontainer uses the Java 25 image and also enables the desktop-lite feature, which gives the container a real DISPLAY and a browser interface at http://127.0.0.1:6080 (password: vscode).

The F5 workflow runs two Gradle processes side by side:

- gradlew -t classes: recompiles on file save
- gradlew run -Pdev: starts the app with -Dio.ktor.development=true and opens JDWP on 127.0.0.1:5005

This keeps the app alive across reloads while keeping plain ./gradlew run production-like.

VS Code Gradle tasks set `JAVA_HOME` to the container's JDK at `/usr/lib/jvm/msopenjdk-current`. This overrides host-specific Java paths that extensions may inject into task terminals. If a manual terminal has an invalid `JAVA_HOME`, run `export JAVA_HOME=/usr/lib/jvm/msopenjdk-current` before `./gradlew`.

Auto-reload depends on modules being declared by name in src/main/resources/application.yaml, which is how the template is wired.

## Debugging

The template is set up for two attach-based debug flows:

- F5 / Debug Ktor: attaches to the app on port 5005
- Debug tests: attaches to the test JVM on port 5007

The debug ports are intentionally separate because Gradle --debug-jvm also uses 5005 by default.

## Test suite

The project ships with three test groups:

- unit: in-process Ktor tests using testApplication, plus fast no-Ktor tests of the domain/application layers
- arch: Konsist tests asserting the hexagonal layer dependency direction (domain -> nothing, application -> domain, adapter -> application/domain)
- browser: Playwright tests against the running HTTP server, including API and Swagger UI behavior

Run them with:

```bash
./gradlew test        # unit + arch tests
./gradlew browserTest # Playwright HTTP and browser tests
```

Behavior of the browser tests:

- if BASE_URL is set, tests hit the already-running app, so app breakpoints still work;
- otherwise, the test JVM starts its own embedded server and shares the same JVM context for debugging.

The browser suite is separated because Chromium startup is slow and not needed for the normal build.

When DISPLAY is available, the browser test runs headed and can be watched through the noVNC desktop; without DISPLAY it falls back to headless mode automatically.

## Build, packaging, and quality checks

Common commands:

```bash
./gradlew build             # compile, lint, and run project verification
./gradlew test              # run unit + arch tests
./gradlew browserTest       # run Playwright HTTP and browser suite (downloads Chromium if missing, no sudo)
./gradlew playwrightInstallWithDeps  # Chromium + OS libraries via apt (needs sudo; devcontainer/CI only)
./gradlew formatKotlin      # auto-fix ktlint issues
./gradlew lintKotlin        # lint-only check
./gradlew run               # plain app run, no dev-mode reload
./gradlew run -Pdev         # dev-mode run with auto-reload + JDWP
./gradlew buildFatJar       # fat jar packaging
./gradlew buildImage        # container image creation via Ktor plugin (needs a Docker daemon)
./gradlew koverHtmlReport   # coverage report
```

The repo also includes VS Code tasks for:

- dev
- dev: watch
- dev: server
- dev: stop
- build
- test
- test: current file
- test: watch
- test: open report
- test: debug
- format

## Quality gates and automation

- ktlint is used via the Kotlin linter plugin.
- Kover is configured to exclude browser tests from coverage tasks so normal build/check stays fast.
- A pre-commit hook runs formatKotlin, re-stages the fixed files (except ones with unstaged changes, which get a warning), then runs lintKotlin and the unit/arch tests before the commit is accepted.
- The devcontainer configures core.hooksPath to .githooks automatically.
- CI (.github/workflows/ci.yml) runs build, Kover, a fat-jar smoke test and browserTest on pushes to main and PRs; Dependabot keeps Gradle, GitHub Actions and devcontainer versions current.

## Logging

The app includes two logback profiles:

- logback.xml: human-readable console logs and request correlation through CallId and X-Request-Id
- logback-json.xml: structured JSON output for log aggregation systems

To use JSON logging when running the packaged jar:

```bash
java -Dlogback.configurationFile=logback-json.xml -jar build/libs/ktor-template-all.jar
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).

## Summary

This template is designed to feel like a working Ktor service immediately after opening the repo in a devcontainer: the app runs, the debugger is attached, the browser can be watched, and the quality checks are already in place.

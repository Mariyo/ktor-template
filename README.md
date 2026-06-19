# Ktor Template

Opinionated starter for a Kotlin + Ktor backend with three development paths:

1. VS Code Dev Container (best zero-setup team experience)
2. VS Code + local JDK
3. IntelliJ IDEA (local or Docker)

## Quick Start (Recommended)

Use this if you want the fastest path with the fewest machine-specific issues.

1. Open the project in VS Code.
2. Run `Dev Containers: Reopen in Container`.
3. Run task `Devcontainer: Start Dev Loop`.
4. Open [http://ktor-template.localhost/](http://ktor-template.localhost/) or [http://localhost:8080/](http://localhost:8080/).
5. Make a code change in `src/main/kotlin` and refresh the browser.
6. When done, run task `Devcontainer: Stop Dev Loop`.

## Prerequisites

- Docker: https://www.docker.com/
- JDK 21 (required for non-container local development)
- VS Code extension: Dev Containers (`ms-vscode-remote.remote-containers`)

## Choose Your Workflow

### A) VS Code in Dev Container

Best for team consistency and minimal local setup.

1. Reopen project in container (`Dev Containers: Reopen in Container`).
2. Run task `Devcontainer: Start Dev Loop`.
3. Open [http://ktor-template.localhost/](http://ktor-template.localhost/).
4. Stop with task `Devcontainer: Stop Dev Loop`.

### B) VS Code with local JDK

Best when you prefer native local execution.

1. Ensure Java 21 is active.
2. Run:

```bash
./gradlew run --no-daemon
```

3. Open [http://ktor-template.localhost/](http://ktor-template.localhost/) or [http://localhost:8080/](http://localhost:8080/).

Useful commands:

```bash
./gradlew classes -t -x test --no-daemon
./gradlew test --no-daemon
./gradlew clean --no-daemon
```

### C) IntelliJ IDEA

1. Run the `Run` configuration for local app development.
2. Open `http://localhost:8080/`.
3. Edit code in `src/main/kotlin`.
4. Rebuild on change with `Ctrl+F9` (or your build-on-change config).

## Docker Compose Localhost Stack

Use this when you want containerized app + local hostname routing.

### Start from VS Code task

1. Run task `Localhost: Ktor Template - Start containers`.
2. Open [http://ktor-template.localhost/](http://ktor-template.localhost/).
3. Stop with task `Localhost: Ktor Template - Stop containers`.

### Start from terminal

```bash
docker compose -f infra/localhost/docker-compose.yaml up --build
```

Stop:

```bash
docker compose -f infra/localhost/docker-compose.yaml down
```

## Debugging

VS Code launch configurations:

- `Kotlin Attach (Dev Container)`
- `Kotlin Attach (Docker Compose)`

If pre-launch tasks fail or are missing, start the related run task manually first, then attach the debugger.

## VS Code Tasks Reference

- `Devcontainer: Build`: Build classes
- `Devcontainer: Watch Classes`: Watch and rebuild classes continuously
- `Devcontainer: Run App`: Run Ktor app
- `Devcontainer: Start Dev Loop`: Start watch + app in parallel
- `Devcontainer: Stop Dev Loop`: Stop dev loop Gradle processes
- `Devcontainer: Test`: Run tests
- `Devcontainer: Clean`: Clean build outputs
- `Localhost: Ktor Template - Start containers`: Start Docker Compose stack
- `Localhost: Ktor Template - Stop containers`: Stop Docker Compose stack

## Troubleshooting

### Dev loop task exits with code 1

1. Run `Devcontainer: Stop Dev Loop`.
2. Run `Devcontainer: Build` and fix any compile errors.
3. Start again with `Devcontainer: Start Dev Loop`.

### Port already in use

- Confirm no old Gradle run process is alive.
- Re-run `Devcontainer: Stop Dev Loop`.
- Retry start task.

### Docker hostname does not resolve

- Ensure Docker Compose stack is running.
- Verify [http://ktor-template.localhost/](http://ktor-template.localhost/) directly in browser.
- Fall back to `http://localhost:8080/` when running local app.

## Project Paths

- Source code: `src/main/kotlin`
- Resources: `src/main/resources`
- Docker compose: `infra/localhost/docker-compose.yaml`

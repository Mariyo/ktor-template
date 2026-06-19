# Ktor Template

Opinionated starter for a Kotlin + Ktor backend with two development paths:

1. VS Code Dev Container (best zero-setup team experience)
2. IntelliJ IDEA (local or Docker)

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

#### Debugging

VS Code launch configurations:

- `Kotlin Attach (Dev Container)`
- `Kotlin Attach (Docker Compose)`

### B) IntelliJ IDEA

1. Run the `Run` configuration for local app development.
2. Open `http://localhost:8080/`.
3. Edit code in `src/main/kotlin`.
4. Rebuild on change with `Ctrl+F9` (or your build-on-change config).






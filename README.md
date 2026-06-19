# Ktor Template

Starter template for a Kotlin + Ktor backend with three supported development workflows.

## Tutorials

### First run in VS Code Dev Container (recommended)

This tutorial gets you from clone to a running app with the most consistent team setup.

1. Open the repository in VS Code.
2. Run Command Palette action `Dev Containers: Reopen in Container`.
3. Run task `Devcontainer: Start Dev Loop`.
4. Open [http://localhost:8080/](http://localhost:8080/) or [http://ktor-template.localhost/](http://ktor-template.localhost/).
5. Edit a file in `src/main/kotlin` and save.
6. Refresh the page and confirm the change is reflected.
7. Stop the loop with task `Devcontainer: Stop Dev Loop`.

Expected result: app is reachable and rebuilds happen automatically while coding.

## How-to Guides

### How to develop in VS Code Dev Container (A)

1. Reopen the workspace in container.
2. Run task `Devcontainer: Start Dev Loop`.
3. Code in `src/main/kotlin`.
4. Stop with task `Devcontainer: Stop Dev Loop`.

### How to develop in IntelliJ IDEA locally (B)

1. Ensure JDK 21 is available.
2. Run IntelliJ configuration `Run`.
3. Open [http://localhost:8080/](http://localhost:8080/).
4. Rebuild on change with `Ctrl+F9` (or your build-on-change setup).

### How to develop in IntelliJ or VS Code with Docker Compose runtime (C)

Use this when you want IDE editing but application runtime inside Docker Compose from `infra/localhost`.

1. Start containers:

```bash
docker compose -f infra/localhost/docker-compose.yaml up --build
```

Alternative in VS Code: run task `Localhost: Ktor Template - Start containers`.

2. Open [http://ktor-template.localhost/](http://ktor-template.localhost/).
3. Keep editing in IntelliJ or VS Code.
4. Attach debugger when needed:
   - VS Code: `Kotlin Attach (Docker Compose)`
   - IntelliJ IDEA: your `Remote Debug` configuration
5. Stop containers:

```bash
docker compose -f infra/localhost/docker-compose.yaml down
```

Alternative in VS Code: run task `Localhost: Ktor Template - Stop containers`.

### How to run tests

1. VS Code task: `Devcontainer: Test`.
2. Terminal alternative:

```bash
./gradlew test --no-daemon
```

### How to troubleshoot failed dev loop tasks

1. Run `Devcontainer: Stop Dev Loop`.
2. Run `Devcontainer: Build` and fix compile errors.
3. Start again with `Devcontainer: Start Dev Loop`.

## Reference

### Prerequisites

- Docker: https://www.docker.com/
- JDK 21 for local JVM workflow
- VS Code extension: Dev Containers (`ms-vscode-remote.remote-containers`)

### URLs and ports

- Local app URL: [http://localhost:8080/](http://localhost:8080/)
- Compose app URL: [http://ktor-template.localhost/](http://ktor-template.localhost/)
- Debug port: `5005`

### VS Code tasks

- `Devcontainer: Build`
- `Devcontainer: Watch Classes`
- `Devcontainer: Run App`
- `Devcontainer: Start Dev Loop`
- `Devcontainer: Stop Dev Loop`
- `Devcontainer: Test`
- `Devcontainer: Clean`
- `Localhost: Ktor Template - Start containers`
- `Localhost: Ktor Template - Stop containers`

### VS Code launch configurations

- `Kotlin Attach (Dev Container)`
- `Kotlin Attach (Docker Compose)`

### Important paths

- Source code: `src/main/kotlin`
- Resources: `src/main/resources`
- Compose file: `infra/localhost/docker-compose.yaml`

## Explanation

### Which workflow to choose

- A (VS Code Dev Container): best default for predictable team environment.
- B (IntelliJ local): fastest startup when local JVM setup is already stable.
- C (IDE + Compose runtime): best environment parity with containerized runtime.

### Why the dev loop is split into two tasks

`Devcontainer: Start Dev Loop` runs watch + app tasks in parallel so compilation and runtime concerns stay separate.

This gives faster feedback than full restarts and keeps logs easier to inspect.






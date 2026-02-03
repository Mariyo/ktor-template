# Template for Ktor backend

## Development Setup in IntelliJ IDEA

1. Run application by `Run` configuration
2. Open `http://localhost:8080/` in browser.
3. Change code in `src/main/kotlin` and see changes in browser.
4. Run `Build on change` configuration or just hit `CTRL + F9`

## Development Setup in IntelliJ IDEA and Docker

1. Run application by `Run in container` configuration.
2. Run `Remote Debug` to attach debugger to application in container.
3. Open `http://ktor-template.localhost/` in browser.
4. Change code in `src/main/kotlin` and see changes in browser.
5. Run `Build on change` configuration or just hit `CTRL + F9`

### Stop containers

```
cd infra/localhost && docker compose down
```

## Development Setup in VS Code

### Prerequisites

- [Docker](https://www.docker.com/) (for container-based development)
- JDK 21
- VS Code extensions (recommended extensions will be suggested when you open the project):
  - Kotlin Language (`fwcd.kotlin`)
  - Kotlin Language (`mathiasfrohlich.kotlin`)
  - Docker (`ms-azuretools.vscode-docker`)
  - Gradle for Java (`vscjava.vscode-gradle`)

### Running the Application Locally

1. **Run the application:** Press `F5` or go to Run & Debug panel and select `Run Ktor Application`.
   - This will build and run the application locally.
   - Open `http://localhost:8080/` in browser.
2. **Build on change:** Use the task `Ktor Template - Build on change` (Ctrl+Shift+P → Tasks: Run Task).
3. **Run tests:** Press `Ctrl+Shift+T` or use the task `Ktor Template - Test`.

### Running the Application in a Container

1. **Start application:** Open the Command Palette (`Ctrl + Shift + P`), type `Run Task`, and select `Ktor Template - Start containers`. 
   - Container configuration is in `infra/localhost/`.
   - Open `http://ktor-template.localhost/` in browser.
2. **Debugging:** Go to the Run & Debug panel and select `Kotlin Attach (Remote Debug)` to start a remote debugging session.
3. **Build on change:** Use the task `Ktor Template - Build on change`.
4. **Stop the containers:** Use the task `Ktor Template - Stop containers`.

### Available Tasks

All tasks can be run via the Command Palette (`Ctrl+Shift+P` → Tasks: Run Task) or the Terminal menu:

- **Ktor Template - Build** - Build the application (default build task: `Ctrl+Shift+B`)
- **Ktor Template - Build on change** - Continuously build on code changes
- **Ktor Template - Test** - Run tests (default test task)
- **Ktor Template - Clean** - Clean build artifacts
- **Ktor Template - Start containers** - Start Docker containers
- **Ktor Template - Stop containers** - Stop Docker containers

### Workspace Settings

The project includes VS Code workspace settings in `.vscode/settings.json`:
- Automatic Gradle build configuration updates
- Hidden build and temporary directories
- Kotlin language server enabled
- JVM target set to 21
- Organize imports on save

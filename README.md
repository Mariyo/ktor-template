# Template for Ktor backend

## Development Setup in IntelliJ IDEA

1. Run application by `Run` configuration
2. Open `http://localhost:8080/` in browser.
3. Change code in `src/main/kotlin` and see changes in browser.
4. Run `Build on change` configuration or just hit `CTRL + F9`

## Development Setup in IntelliJ IDEA and Docker

1. Run application by `Run in container` configuration.
2. Run `Remote Debug` to attach debugger to application in container.
3. Open `http://ktor-template:8080/` in browser.
4. Change code in `src/main/kotlin` and see changes in browser.
5. Run `Build on change` configuration or just hit `CTRL + F9`

## Development Setup in VS Code

### Prerequisites

- [VS Code](https://code.visualstudio.com/)
- [Docker](https://www.docker.com/)
- Recommended VS Code extensions:
  - Kotlin Language
  - Docker
  - Gradle for Java

### Running the Application in a Container

1. **Start the containers**  
   Open the Command Palette (<kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>P</kbd>), type `Run Task`, and select `Ktor Template - Start containers`. Container configuration is in `infra/localhost/`.

2. **Stop the containers**  
   Use the task `Ktor Template - Stop containers`.

### Debugging

In VS Code, go to the Run & Debug panel and select `Kotlin Attach` to start a remote debugging session.

### Building Locally

- To build the project and watch for changes, run the task `Ktor Template - Build on change`.

### Useful Tasks

- All tasks are defined in `.vscode/tasks.json` and can be run via the Command Palette or the Terminal menu.

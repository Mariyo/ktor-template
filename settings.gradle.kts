// Auto-provisions JDK 25 for the `jvmToolchain(25)` requirement outside the devcontainer.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ktor-template"

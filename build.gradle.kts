plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.kover)
    application
}

group = "com.example"
version = "0.1.0"

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinx.html.jvm)
    implementation(libs.micrometer.registry.prometheus)
    runtimeOnly(libs.logback.classic)
    // Only loaded when JSON logging is selected (see logback-json.xml); harmless to always have on the classpath.
    runtimeOnly(libs.logstash.logback.encoder)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.playwright)
    // Arch tests (com.example.arch) assert the hexagonal layer dependency direction at test time.
    testImplementation(libs.konsist)
}

application {
    mainClass = "com.example.adapter.http.ApplicationKt"
}

kover {
    currentProject {
        instrumentation {
            // Browser startup is kept out of coverage tasks so check/build stay fast and deterministic.
            disabledForTestTasks.add("browserTest")
        }
    }
}

tasks.named<JavaExec>("run") {
    // -Pdev keeps plain `./gradlew run` free of dev-mode reload and an open debug port.
    if (project.hasProperty("dev")) {
        jvmArgs(
            "-Dio.ktor.development=true",
            // Loopback-only: VS Code's port forwarding reaches it from inside the container.
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:5005",
        )
    }
}

tasks.test {
    useJUnitPlatform()
    // Browser tests are kept out of the normal suite because Chromium is an environment-level dependency.
    exclude("com/example/browser/**")
    // Full stacktraces and pass/fail/skip events; Gradle's default output is nearly silent.
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = true
    }
    // --debug-jvm defaults to port 5005, which collides with the app's own JDWP port.
    debugOptions {
        port = 5007
    }
}

// Browser tests stay explicit because Chromium is an environment-level dependency.
tasks.register<Test>("browserTest") {
    description = "Playwright browser tests against a running app."
    group = "verification"
    dependsOn("playwrightInstall")
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    include("com/example/browser/**")
    // A manually requested browser run must execute even when compiled inputs are unchanged.
    outputs.upToDateWhen { false }
    shouldRunAfter(tasks.test)
}

// Idempotent: downloads the Chromium binary + OS deps into the cache volume mounted in devcontainer.json.
tasks.register<JavaExec>("playwrightInstall") {
    description = "Downloads the Playwright Chromium browser and its OS-level dependencies."
    group = "verification"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "com.microsoft.playwright.CLI"
    args("install", "--with-deps", "chromium")
}

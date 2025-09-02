plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.1"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "3.1.1"
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-compression:${ktorVersion}")
    implementation("io.ktor:ktor-server-html-builder:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.12.0}")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:2025.9.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")
}

tasks.test {
    useJUnitPlatform()
}

val isDevelopment = (System.getenv("APP_ENV") ?: "DEVELOPMENT").equals("DEVELOPMENT", ignoreCase = true)

kotlin {
    jvmToolchain(21)
    application {
        mainClass.set("com.example.ApplicationKt")
        applicationDefaultJvmArgs = if (isDevelopment) listOf(
            "-Dio.ktor.development=true",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
        ) else emptyList()
    }
}

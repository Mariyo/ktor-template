plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
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
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
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

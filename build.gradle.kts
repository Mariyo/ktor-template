plugins {
    val gradleKotlinJvmPlugin: String by System.getProperties()
    kotlin("jvm") version gradleKotlinJvmPlugin

    val gradleKtorPlugin: String by System.getProperties()
    id("io.ktor.plugin") version gradleKtorPlugin

    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val ktorVersion: String by project
    implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-compression:${ktorVersion}")
    implementation("io.ktor:ktor-server-html-builder:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")

    val kotlinxVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-html:${kotlinxVersion}")

    val kotlinCssJvmVersion: String by project
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css-jvm:${kotlinCssJvmVersion}")

    val logbackVersion: String by project
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    application {
        mainClass.set("com.example.ApplicationKt")
    }
}
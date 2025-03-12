package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        module = Application::applicationModule, // Due to: https://youtrack.jetbrains.com/issue/KTOR-8313
        watchPaths = listOf("classes", "resources"),
    ).start(wait = true)
}



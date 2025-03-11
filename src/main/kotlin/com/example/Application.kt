package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        module = {
            applicationModule()
        },
        watchPaths = listOf("classes", "resources"),
    ).start(wait = true)
}



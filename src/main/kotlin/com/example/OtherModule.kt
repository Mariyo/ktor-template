package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.otherModule() {
    routing {
        get("/other") {
            call.respondText("Hello from other side!")
        }
    }
}

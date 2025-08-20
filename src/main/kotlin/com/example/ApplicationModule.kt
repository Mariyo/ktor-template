package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.example")

fun Application.applicationModule() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Unhandled error in webs module", cause)
            call.respondText(text = "500: Internal Server Error", status = HttpStatusCode.InternalServerError)
        }
        status(HttpStatusCode.NotFound) { call, status ->
            logger.error("Not found: ${call.request.uri}")
            call.respondText(text = "404: Page Not Found", status = status)
        }
    }
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
    routing {
        get("/") {
            call.respondText("Hello Ktor! man")
        }
        get("/health") {
            call.respondText("OK")
        }
    }
    otherModule()
}

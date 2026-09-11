package com.example.adapter.http

import com.example.application.GreetingService
import com.example.domain.Name
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

// Wire shape for GET /; kept separate from the domain Greeting so domain stays serialization-free.
@Serializable
data class GreetingResponse(val message: String)

// Machine-readable shape for StatusPages so clients get JSON instead of free text on failure.
@Serializable
data class ErrorResponse(val error: String, val status: Int)

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val metrics = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val greetingService = GreetingService()

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
            },
        )
    }
    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
    }
    install(CallLogging) {
        callIdMdc("call-id")
        // Scrapes happen every few seconds; logging each one at INFO would drown out real traffic.
        filter { call -> call.request.path() != "/metrics" }
    }
    install(MicrometerMetrics) { registry = metrics }
    // Compression is opt-in per client (Accept-Encoding), so it's safe to enable unconditionally.
    install(Compression) {
        gzip { priority = 1.0 }
        deflate {
            priority = 10.0
            minimumSize(1024) // Skip compressing tiny bodies where the framing overhead isn't worth it.
        }
    }
    install(StatusPages) {
        // Domain invariants (e.g. Name) surface as IllegalArgumentException; that's a client
        // mistake (400), distinct from the catch-all 500 below for genuinely unexpected failures.
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(cause.message ?: "Invalid request", HttpStatusCode.BadRequest.value),
            )
        }
        // Without this, dev mode returns the raw stacktrace to the client instead of just logging it.
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal server error", HttpStatusCode.InternalServerError.value),
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            // Browsers need a human-readable document here, while API failures keep their JSON contract.
            call.respondText(notFoundPage(status), ContentType.Text.Html, status)
        }
    }

    routing {
        get("/") {
            // Adapter parses wire input into a domain value object (validation lives in Name, not here)
            // and maps the domain result back to the wire DTO; domain stays framework-free.
            val name = call.request.queryParameters["name"]?.let { Name(it) }
            val greeting = greetingService.greet(name)
            call.respond(GreetingResponse(greeting.message))
        }
        // Liveness: process is up. Readiness: process can serve traffic (add dependency checks here).
        get("/health/live") {
            call.respondText("OK")
        }
        get("/health/ready") {
            call.respondText("OK")
        }
        get("/metrics") { call.respondText(metrics.scrape()) }
        // Also the target of the browser test in src/test/kotlin/com/example/browser.
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}

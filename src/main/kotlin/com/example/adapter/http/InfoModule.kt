package com.example.adapter.http

import com.example.application.ServiceInfoService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

// Wire shape for GET /info; kept separate from the domain ServiceInfo so domain stays serialization-free.
@Serializable
data class ServiceInfoResponse(val name: String, val version: String)

// Second, independently YAML-declared module: proves application.yaml's module list (not
// code) drives wiring, so a regression there is caught by tests instead of only in prod.
fun Application.infoModule() {
    val serviceInfoService = ServiceInfoService()

    routing {
        get("/info") {
            // Adapter maps the domain value object to the wire DTO; domain stays framework-free.
            val serviceInfo = serviceInfoService.getServiceInfo()
            call.respond(ServiceInfoResponse(serviceInfo.name, serviceInfo.version))
        }
    }
}

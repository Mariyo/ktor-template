package com.example.unit

import com.example.adapter.http.GreetingResponse
import com.example.adapter.http.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `root returns a JSON greeting`() = testApplication {
        application { module() }

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        // Parsed, not a literal string compare: prettyPrint=true means the wire format has
        // newlines/indentation, only the decoded value is stable.
        assertEquals(
            GreetingResponse("Hello from Ktor!"),
            Json.decodeFromString(GreetingResponse.serializer(), response.bodyAsText()),
        )
    }

    @Test
    fun `root with a name query param returns a personalized JSON greeting`() = testApplication {
        application { module() }

        val response = client.get("/?name=Ada")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            GreetingResponse("Hello, Ada!"),
            Json.decodeFromString(GreetingResponse.serializer(), response.bodyAsText()),
        )
    }

    @Test
    fun `root with a blank name query param returns 400`() = testApplication {
        application { module() }

        val response = client.get("/?name=%20%20")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}

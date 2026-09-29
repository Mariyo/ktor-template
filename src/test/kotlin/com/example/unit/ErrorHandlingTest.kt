package com.example.unit

import com.example.adapter.http.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ErrorHandlingTest {
    @Test
    fun `Ktor BadRequestException maps to 400 instead of 500`() = testApplication {
        application {
            module()
            routing { get("/test/bad-request") { throw BadRequestException("malformed body") } }
        }

        assertEquals(HttpStatusCode.BadRequest, client.get("/test/bad-request").status)
    }

    @Test
    fun `Ktor NotFoundException maps to 404 instead of 500`() = testApplication {
        application {
            module()
            routing { get("/test/missing") { throw NotFoundException("no such thing") } }
        }

        assertEquals(HttpStatusCode.NotFound, client.get("/test/missing").status)
    }

    @Test
    fun `IllegalArgumentException from outside the domain is a 500 and does not leak its message`() = testApplication {
        application {
            module()
            routing { get("/test/boom") { throw IllegalArgumentException("secret internal detail") } }
        }

        val response = client.get("/test/boom")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertFalse("secret internal detail" in response.bodyAsText())
    }
}

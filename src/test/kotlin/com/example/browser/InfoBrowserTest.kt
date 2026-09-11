package com.example.browser

import com.example.adapter.http.ServiceInfoResponse
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

// Only this tier boots via application.yaml's module list, so it's the one place a module
// missing from that YAML (but still present in code) would actually fail a test.
class InfoBrowserTest : AbstractBrowserTest() {
    @Test
    fun `info module wired through application yaml responds over real HTTP`() {
        withApiClient { client ->
            val response = client.get(url("/info"))

            assertEquals(200, response.status())
            assertEquals(
                ServiceInfoResponse("ktor-template", "0.1.0"),
                Json.decodeFromString(ServiceInfoResponse.serializer(), response.text()),
            )
        }
    }
}

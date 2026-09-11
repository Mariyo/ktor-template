package com.example.browser

import kotlin.test.Test
import kotlin.test.assertEquals

class HealthBrowserTest : AbstractBrowserTest() {
    @Test
    fun `liveness and readiness respond over real HTTP`() {
        withApiClient { client ->
            val live = client.get(url("/health/live"))
            val ready = client.get(url("/health/ready"))

            assertEquals(200, live.status())
            assertEquals(200, ready.status())
        }
    }
}

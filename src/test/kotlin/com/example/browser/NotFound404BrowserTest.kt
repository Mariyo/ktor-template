package com.example.browser

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class NotFound404BrowserTest : AbstractBrowserTest() {
    @Test
    fun `unknown route returns a styled HTML 404`() {
        withBrowser(headed = false) { browser ->
            val page = browser.newPage()
            val response = page.navigate(url("/unknown-route"))
            val body = page.content()
            assertContains(body, "<title>404 | Page not found</title>")
            assertContains(body, "href=\"/\">Return home</a>")
            assertEquals(404, response?.status())
        }
    }
}

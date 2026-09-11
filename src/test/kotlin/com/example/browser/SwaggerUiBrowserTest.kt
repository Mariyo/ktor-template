package com.example.browser

import kotlin.test.Test
import kotlin.test.assertTrue

// Only browser scenario in the suite: proves the app serves its own OpenAPI page end-to-end.
// DISPLAY is set by the devcontainer's desktop-lite feature -> headed + slowed down so the run
// is watchable live at localhost:6080 (noVNC). No DISPLAY (e.g. CI) -> headless, full speed.
class SwaggerUiBrowserTest : AbstractBrowserTest() {
    @Test
    fun `swagger ui loads`() {
        withBrowser { browser ->
            val page = browser.newPage()
            page.navigate(url("/swagger"))
            page.waitForSelector(".swagger-ui")

            assertTrue(page.locator(".swagger-ui").count() > 0)
        }
    }
}

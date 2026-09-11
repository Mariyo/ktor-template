package com.example.browser

import com.microsoft.playwright.APIRequestContext
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking

abstract class AbstractBrowserTest {
    protected fun url(path: String): String = "$baseUrl$path"

    protected fun withApiClient(block: (APIRequestContext) -> Unit) {
        withPlaywright { playwright ->
            val client = playwright.request().newContext()
            try {
                block(client)
            } finally {
                client.dispose()
            }
        }
    }

    protected fun withBrowser(headed: Boolean = true, block: (Browser) -> Unit) {
        withPlaywright { playwright ->
            launchBrowser(playwright, headed).use(block)
        }
    }

    private fun withPlaywright(block: (Playwright) -> Unit) {
        Playwright.create().use(block)
    }

    private fun launchBrowser(playwright: Playwright, headed: Boolean): Browser {
        val launchOptions = BrowserType.LaunchOptions().setArgs(containerArgs)
        if (headed && !System.getenv("DISPLAY").isNullOrBlank()) {
            return try {
                playwright.chromium().launch(
                    launchOptions
                        .setHeadless(false)
                        .setSlowMo(250.0),
                )
            } catch (e: Exception) {
                System.err.println("Headed launch failed (${e.message}), falling back to headless.")
                playwright.chromium().launch(launchOptions.setHeadless(true))
            }
        }
        return playwright.chromium().launch(launchOptions.setHeadless(true))
    }

    private companion object {
        val containerArgs = listOf("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu")

        val baseUrl: String by lazy {
            System.getenv("BASE_URL")?.trimEnd('/') ?: startEmbedded()
        }

        fun startEmbedded(): String {
            val server = EngineMain.createServer(arrayOf("-port=0")).start(wait = false)
            val port = runBlocking { server.engine.resolvedConnectors().first().port }
            Runtime.getRuntime().addShutdownHook(Thread { server.stop(1000, 2000) })
            return "http://localhost:$port"
        }
    }
}

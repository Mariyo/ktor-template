package com.example.arch

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import kotlin.test.Test
import kotlin.test.assertTrue

// Encodes the hexagonal rule as an executable check instead of only prose in ARCHITECTURE.md.
class HexagonalArchitectureTest {
    // Layer package patterns mirror the folders under src/main/kotlin/com/example.
    private val domain = Layer("Domain", "com.example.domain..")
    private val application = Layer("Application", "com.example.application..")
    private val adapter = Layer("Adapter", "com.example.adapter..")

    @Test
    fun `layers depend only in the adapter to application to domain direction`() {
        // scopeFromProduction excludes this very test file, so the rule can't accidentally check itself.
        Konsist.scopeFromProduction().assertArchitecture {
            domain.dependsOnNothing()
            application.dependsOn(domain)
            adapter.dependsOn(application, domain)
        }
    }

    @Test
    fun `domain and application layers do not import Ktor`() {
        // Belt-and-suspenders on top of assertArchitecture: catches framework leakage even via
        // packages assertArchitecture wouldn't flag (e.g. a stray import with no matching layer call).
        val violations = Konsist.scopeFromProduction()
            .files
            .filter { file ->
                val packageName = file.packagee?.name.orEmpty()
                packageName.startsWith("com.example.domain") || packageName.startsWith("com.example.application")
            }
            .filter { file -> file.hasImport { import -> import.name.startsWith("io.ktor") } }

        assertTrue(
            violations.isEmpty(),
            "Domain/application files must not import Ktor: ${violations.map { it.path }}",
        )
    }
}

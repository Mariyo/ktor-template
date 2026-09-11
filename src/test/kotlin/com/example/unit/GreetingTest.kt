package com.example.unit

import com.example.domain.Greeting
import kotlin.test.Test
import kotlin.test.assertFailsWith

class GreetingTest {
    @Test
    fun `blank message is rejected`() {
        assertFailsWith<IllegalArgumentException> { Greeting("   ") }
    }
}

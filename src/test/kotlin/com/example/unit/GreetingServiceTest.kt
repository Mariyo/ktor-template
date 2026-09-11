package com.example.unit

import com.example.application.GreetingService
import com.example.domain.Greeting
import com.example.domain.Name
import kotlin.test.Test
import kotlin.test.assertEquals

// Domain/application layers have zero Ktor deps, so this runs without testApplication at all.
class GreetingServiceTest {
    @Test
    fun `greet without a name returns the default greeting`() {
        assertEquals(Greeting("Hello from Ktor!"), GreetingService().greet())
    }

    @Test
    fun `greet with a name returns a personalized greeting`() {
        assertEquals(Greeting("Hello, Ada!"), GreetingService().greet(Name("Ada")))
    }
}

package com.example.application

import com.example.domain.Greeting
import com.example.domain.Name

// Use case: the only place that decides the greeting text, kept out of the HTTP adapter.
class GreetingService {
    fun greet(name: Name? = null): Greeting = if (name == null) Greeting("Hello from Ktor!") else Greeting("Hello, ${name.value}!")
}

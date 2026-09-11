package com.example.domain

// Plain value object: domain layer stays free of Ktor/serialization so it has zero framework deps.
data class Greeting(val message: String) {
    init {
        // Domain invariant: a blank greeting is not a valid greeting, regardless of caller (HTTP, test, ...).
        require(message.isNotBlank()) { "Greeting message must not be blank" }
    }
}

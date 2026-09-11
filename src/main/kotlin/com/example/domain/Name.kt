package com.example.domain

// Plain value object: domain layer stays free of Ktor/serialization so it has zero framework deps.
data class Name(val value: String) {
    init {
        // Domain invariant: whoever we greet must have an actual, reasonably-sized name.
        require(value.isNotBlank()) { "Name must not be blank" }
        require(value.length <= MAX_LENGTH) { "Name must not exceed $MAX_LENGTH characters" }
    }

    companion object {
        const val MAX_LENGTH = 100
    }
}

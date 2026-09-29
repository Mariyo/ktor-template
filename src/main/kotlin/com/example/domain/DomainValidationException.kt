package com.example.domain

// Distinct from plain IllegalArgumentException so adapters can tell a domain rule violation (client error) from a bug.
class DomainValidationException(message: String) : IllegalArgumentException(message)

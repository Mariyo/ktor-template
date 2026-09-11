package com.example.domain

// Plain value object: domain layer stays free of Ktor/serialization so it has zero framework deps.
data class ServiceInfo(val name: String, val version: String)

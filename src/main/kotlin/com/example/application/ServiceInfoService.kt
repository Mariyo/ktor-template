package com.example.application

import com.example.domain.ServiceInfo

// Use case: the only place that knows this service's identity/version.
class ServiceInfoService {
    fun getServiceInfo(): ServiceInfo = ServiceInfo(name = "ktor-template", version = "0.1.0")
}

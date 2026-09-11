package com.example.unit

import com.example.application.ServiceInfoService
import com.example.domain.ServiceInfo
import kotlin.test.Test
import kotlin.test.assertEquals

// Domain/application layers have zero Ktor deps, so this runs without testApplication at all.
class ServiceInfoServiceTest {
    @Test
    fun `getServiceInfo returns this service's identity`() {
        assertEquals(ServiceInfo("ktor-template", "0.1.0"), ServiceInfoService().getServiceInfo())
    }
}

package com.example.unit

import com.example.domain.DomainValidationException
import com.example.domain.Name
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NameTest {
    @Test
    fun `blank name is rejected`() {
        assertFailsWith<DomainValidationException> { Name("   ") }
    }

    @Test
    fun `name longer than the max length is rejected`() {
        assertFailsWith<DomainValidationException> { Name("a".repeat(Name.MAX_LENGTH + 1)) }
    }
}

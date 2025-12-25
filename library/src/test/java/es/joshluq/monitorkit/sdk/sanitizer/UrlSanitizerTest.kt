package es.joshluq.monitorkit.sdk.sanitizer

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UrlSanitizerTest {

    private lateinit var sanitizer: UrlSanitizer

    @Before
    fun setUp() {
        sanitizer = UrlSanitizer()
    }

    @Test
    fun `sanitize should match explicit wildcard pattern (single segment)`() {
        // Given
        val patterns = listOf("api/users/*/profile")
        sanitizer.configurePatterns(patterns)
        val input = "api/users/12345/profile"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("api/users/*/profile", result)
    }

    @Test
    fun `sanitize should match explicit wildcard pattern (suffix)`() {
        // Given
        val patterns = listOf("auth/**")
        sanitizer.configurePatterns(patterns)
        val input = "auth/v1/login/callback"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("auth/**", result)
    }

    @Test
    fun `sanitize should return generic fallback for numeric IDs when no pattern matches`() {
        // Given
        sanitizer.configurePatterns(emptyList())
        val input = "api/orders/98765/details"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("api/orders/*/details", result)
    }

    @Test
    fun `sanitize should return generic fallback for UUIDs when no pattern matches`() {
        // Given
        sanitizer.configurePatterns(emptyList())
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val input = "api/files/$uuid/download"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("api/files/*/download", result)
    }

    @Test
    fun `sanitize should handle mixed UUID and ID in fallback`() {
        // Given
        sanitizer.configurePatterns(emptyList())
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val input = "api/users/101/documents/$uuid"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("api/users/*/documents/*", result)
    }

    @Test
    fun `sanitize should return original url if safe and no pattern matches`() {
        // Given
        sanitizer.configurePatterns(emptyList())
        val input = "api/config/settings"

        // When
        val result = sanitizer.sanitize(input)

        // Then
        assertEquals("api/config/settings", result)
    }
}

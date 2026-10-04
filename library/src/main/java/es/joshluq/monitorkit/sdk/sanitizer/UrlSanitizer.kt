package es.joshluq.monitorkit.sdk.sanitizer

import java.util.concurrent.ConcurrentHashMap

/**
 * Utility class responsible for sanitizing URLs before they are reported.
 * It prevents sensitive data (IDs, UUIDs, Tokens) from leaking into analytics.
 *
 * It uses a hybrid strategy:
 * 1. **Allowlist Patterns**: Checks if the URL matches a configured pattern.
 * 2. **Generic Fallback**: Uses Regex to replace UUIDs and numeric IDs.
 */
internal class UrlSanitizer {
    private data class PatternEntry(
        val originalPattern: String,
        val regex: Regex,
    )

    @Volatile
    private var compiledPatterns: List<PatternEntry> = emptyList()

    private val uuidRegex = Regex("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
    private val numberRegex = Regex("(?<=/|^)\\d+(?=/|$)")

    /**
     * Configures the list of URL patterns to be used for whitelist matching.
     *
     * Wildcards supported:
     * - `*`: Matches a single segment (e.g., "users/`*`/profile").
     * - `**`: Matches any suffix (e.g., "api/v1/`**`").
     *
     * @param patterns List of path patterns.
     */
    fun configurePatterns(patterns: List<String>) {
        compiledPatterns =
            patterns.map { pattern ->
                val regexString =
                    pattern
                        .replace("?", "\\?")
                        .replace(".", "\\.")
                        .replace("**", "##DOUBLE_WILD##")
                        .replace("*", "##SINGLE_WILD##")
                        .replace("##DOUBLE_WILD##", ".*")
                        .replace("##SINGLE_WILD##", "[^/]+")

                PatternEntry(pattern, Regex("^$regexString$"))
            }
    }

    /**
     * Sanitizes a given URL based on the configured logic.
     *
     * @param url The raw URL or path.
     * @return The sanitized URL.
     */
    fun sanitize(url: String): String {
        val patterns = compiledPatterns
        for (i in patterns.indices) {
            val entry = patterns[i]
            if (entry.regex.matches(url)) {
                return entry.originalPattern
            }
        }

        var hasDigits = false
        for (i in 0 until url.length) {
            if (url[i].isDigit()) {
                hasDigits = true
                break
            }
        }
        if (!hasDigits) return url

        var sanitized = url.replace(uuidRegex, "*")
        sanitized = sanitized.replace(numberRegex, "*")

        return sanitized
    }
}

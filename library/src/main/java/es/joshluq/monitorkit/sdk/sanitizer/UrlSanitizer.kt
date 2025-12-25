package es.joshluq.monitorkit.sdk.sanitizer

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class responsible for sanitizing URLs before they are reported.
 * It prevents sensitive data (IDs, UUIDs, Tokens) from leaking into analytics.
 *
 * It uses a hybrid strategy:
 * 1. **Allowlist Patterns**: Checks if the URL matches a configured pattern.
 * 2. **Generic Fallback**: Uses Regex to replace UUIDs and numeric IDs.
 */
@Singleton
class UrlSanitizer @Inject constructor() {

    private val compiledPatterns = ConcurrentHashMap<String, Regex>()

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
        compiledPatterns.clear()
        patterns.forEach { pattern ->
            val regexString = pattern
                .replace("?", "\\?")
                .replace(".", "\\.")
                .replace("**", "##DOUBLE_WILD##")
                .replace("*", "##SINGLE_WILD##")
                .replace("##DOUBLE_WILD##", ".*")
                .replace("##SINGLE_WILD##", "[^/]+")

            compiledPatterns[pattern] = Regex("^$regexString$")
        }
    }

    /**
     * Sanitizes a given URL based on the configured logic.
     *
     * @param url The raw URL or path.
     * @return The sanitized URL.
     */
    fun sanitize(url: String): String {
        for ((pattern, regex) in compiledPatterns) {
            if (regex.matches(url)) {
                return pattern
            }
        }

        var sanitized = url.replace(uuidRegex, "*")
        sanitized = sanitized.replace(numberRegex, "*")

        return sanitized
    }
}

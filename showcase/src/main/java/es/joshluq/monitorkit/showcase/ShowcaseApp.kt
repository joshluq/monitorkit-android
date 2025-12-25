package es.joshluq.monitorkit.showcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import es.joshluq.monitorkit.sdk.MonitorkitManager
import javax.inject.Inject

@HiltAndroidApp
class ShowcaseApp : Application() {

    @Inject
    lateinit var monitorkitManager: MonitorkitManager

    override fun onCreate() {
        super.onCreate()
        
        // 1. Initialize MonitorkitManager with a provider
        monitorkitManager.addProvider(LogMonitorProvider())

        // 2. Configure URL Sanitization Patterns
        monitorkitManager.configureUrlPatterns(
            listOf(
                "api/users/*/profile", // Matches single segment: api/users/123/profile
                "auth/**",             // Matches any suffix: auth/v1/login
                "v1/catalog/*/items"   // Another example
            )
        )
        
        // Track an initial event
        monitorkitManager.trackEvent("app_initialized", mapOf("module" to "showcase"))
    }
}

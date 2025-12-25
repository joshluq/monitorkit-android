package es.joshluq.monitorkit.showcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import es.joshluq.monitorkit.sdk.MonitorkitManager
import javax.inject.Inject

@HiltAndroidApp
class ShowcaseApp : Application() {

    @Inject
    lateinit var monitorkitManager: MonitorkitManager
    
    @Inject
    lateinit var uiMonitorProvider: UiMonitorProvider

    override fun onCreate() {
        super.onCreate()
        
        // Register providers
        monitorkitManager
            .addProvider(LogMonitorProvider())
            .addProvider(uiMonitorProvider)

        // Configure URL Sanitization Patterns
        monitorkitManager.configureUrlPatterns(
            listOf(
                "api/users/*/profile", 
                "auth/**"
            )
        )
        
        // Track an initial event
        monitorkitManager.trackEvent("app_initialized")
    }
}

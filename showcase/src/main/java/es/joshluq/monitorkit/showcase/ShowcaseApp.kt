package es.joshluq.monitorkit.showcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import es.joshluq.monitorkit.sdk.MonitorKitManager
import javax.inject.Inject

/**
 * Main application class for the Monitorkit Showcase.
 *
 * It uses Hilt for Dependency Injection to provide the [MonitorKitManager]
 * instance across the application.
 */
@HiltAndroidApp
class ShowcaseApp : Application() {

    @Inject
    lateinit var monitorkitManager: MonitorKitManager

    override fun onCreate() {
        super.onCreate()
        
        // Track an initial event to confirm initialization
        monitorkitManager.trackEvent("app_initialized")
    }
}

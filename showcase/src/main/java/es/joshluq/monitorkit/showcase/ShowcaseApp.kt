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
        
        // Initialize MonitorkitManager with a provider
        monitorkitManager.addProvider(LogMonitorProvider())
        
        // Track an initial event
        monitorkitManager.trackEvent("app_initialized", mapOf("module" to "showcase"))
    }
}

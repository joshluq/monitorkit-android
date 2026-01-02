package es.joshluq.monitorkit.showcase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.joshluq.monitorkit.sdk.MonitorkitManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonitorModule {

    @Provides
    @Singleton
    fun provideUiMonitorProvider(): UiMonitorProvider {
        return UiMonitorProvider()
    }

    @Provides
    @Singleton
    fun provideMonitorkitManager(uiMonitorProvider: UiMonitorProvider): MonitorkitManager {
        return MonitorkitManager.Builder()
            .addProvider(LogMonitorProvider())
            .addProvider(uiMonitorProvider)
            .configureUrlPatterns(
                listOf(
                    "api/users/*/profile",
                    "auth/**"
                )
            )
            .setUseNativeTracing(false)
            .build()
    }
}

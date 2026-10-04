package es.joshluq.monitorkit.showcase

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.joshluq.foundationkit.network.NetworkMonitor
import es.joshluq.foundationkit.network.networkMonitor
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
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor {
        return context.networkMonitor()
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

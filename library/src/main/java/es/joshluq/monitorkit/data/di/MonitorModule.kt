package es.joshluq.monitorkit.data.di

import es.joshluq.monitorkit.data.datasource.MonitorDataSource
import es.joshluq.monitorkit.data.datasource.MonitorDataSourceImpl
import es.joshluq.monitorkit.data.repository.MonitorRepositoryImpl
import es.joshluq.monitorkit.domain.repository.MonitorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing monitoring-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MonitorModule {

    @Binds
    @Singleton
    abstract fun bindMonitorDataSource(
        monitorDataSourceImpl: MonitorDataSourceImpl
    ): MonitorDataSource

    @Binds
    @Singleton
    abstract fun bindMonitorRepository(
        monitorRepositoryImpl: MonitorRepositoryImpl
    ): MonitorRepository
}

package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.core.coroutine.DefaultDispatchersProvider
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.core.utils.AndroidAppLogger
import com.cebolao.lotofacil.core.utils.TimeProvider
import com.cebolao.lotofacil.core.utils.SystemTimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDispatchersProvider(): DispatchersProvider = DefaultDispatchersProvider()

    @Provides
    @Singleton
    fun provideAppLogger(): AppLogger = AndroidAppLogger()

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchersProvider.default)
}

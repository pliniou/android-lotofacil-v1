package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.core.cache.MemoryCache
import com.cebolao.lotofacil.core.cache.ReactiveCache
import com.cebolao.lotofacil.core.result.ErrorMessageMapper
import com.cebolao.lotofacil.core.coroutine.DefaultDispatchersProvider
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.core.utils.AndroidAppLogger
import com.cebolao.lotofacil.core.utils.TimeProvider
import com.cebolao.lotofacil.core.utils.SystemTimeProvider
import com.cebolao.lotofacil.domain.repository.HistoryRepository
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

    @Provides
    @Singleton
    fun provideMemoryCache(): MemoryCache = MemoryCache()

    @Provides
    @Singleton
    fun provideReactiveCache(): ReactiveCache = ReactiveCache()

    @Provides
    @Singleton
    fun provideErrorMessageMapper(appLogger: AppLogger): ErrorMessageMapper = 
        ErrorMessageMapper(appLogger)
}

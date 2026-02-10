package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.core.utils.AppLogger
import com.cebolao.lotofacil.data.parser.HistoryParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Random
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {
    @Provides
    @Singleton
    fun provideHistoryParser(logger: AppLogger): HistoryParser = HistoryParser(logger)

    @Provides
    @Singleton
    fun provideRandom(): Random = Random()
}

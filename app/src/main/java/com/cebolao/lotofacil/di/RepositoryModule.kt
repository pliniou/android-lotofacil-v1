package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.data.repository.CheckHistoryRepositoryImpl
import com.cebolao.lotofacil.data.repository.GameRepositoryImpl
import com.cebolao.lotofacil.data.repository.HistoryRepositoryImpl
import com.cebolao.lotofacil.data.repository.StatisticsRepositoryImpl
import com.cebolao.lotofacil.data.repository.UserPreferencesRepositoryImpl
import com.cebolao.lotofacil.domain.repository.CheckHistoryRepository
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        statisticsRepositoryImpl: StatisticsRepositoryImpl
    ): StatisticsRepository

    @Binds
    @Singleton
    abstract fun bindCheckHistoryRepository(
        checkHistoryRepositoryImpl: CheckHistoryRepositoryImpl
    ): CheckHistoryRepository
}

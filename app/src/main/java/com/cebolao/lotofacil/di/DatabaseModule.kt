package com.cebolao.lotofacil.di

import android.content.Context
import androidx.room.Room
import com.cebolao.lotofacil.BuildConfig
import com.cebolao.lotofacil.data.datasource.database.CheckHistoryDao
import com.cebolao.lotofacil.data.datasource.database.GameDao
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.LotofacilDatabase
import com.cebolao.lotofacil.data.datasource.database.StatisticsCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLotofacilDatabase(
        @ApplicationContext context: Context
    ): LotofacilDatabase {
        return Room.databaseBuilder(context, LotofacilDatabase::class.java, "lotofacil_db")
            .apply {
                if (BuildConfig.DEBUG) {
                    // allow data resets in dev for faster iteration
                    fallbackToDestructiveMigration()
                    fallbackToDestructiveMigrationOnDowngrade()
                } else {
                    // use all defined migrations in production to preserve data
                    addMigrations(*LotofacilDatabase.getMigrations())
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: LotofacilDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideCheckHistoryDao(database: LotofacilDatabase): CheckHistoryDao {
        return database.checkHistoryDao()
    }

    @Provides
    @Singleton
    fun provideStatisticsCacheDao(database: LotofacilDatabase): StatisticsCacheDao {
        return database.statisticsCacheDao()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: LotofacilDatabase): GameDao {
        return database.gameDao()
    }
}

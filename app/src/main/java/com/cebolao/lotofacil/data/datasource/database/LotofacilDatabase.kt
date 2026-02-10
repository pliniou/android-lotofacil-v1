package com.cebolao.lotofacil.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cebolao.lotofacil.data.datasource.database.entity.CheckHistoryEntity
import com.cebolao.lotofacil.data.datasource.database.entity.Converters
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity
import com.cebolao.lotofacil.data.datasource.database.entity.StatisticsCacheEntity

@Database(
    entities = [HistoricalDrawEntity::class, CheckHistoryEntity::class, StatisticsCacheEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LotofacilDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun checkHistoryDao(): CheckHistoryDao
    abstract fun statisticsCacheDao(): StatisticsCacheDao

    companion object {
        /**
         * Migrações para atualizações seguras do schema.
         */
        fun getMigrations() = MigrationHelper.getMigrations()
    }
}

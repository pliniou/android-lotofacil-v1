package com.cebolao.lotofacil.data.datasource.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationHelper {

    fun getMigrations(): Array<Migration> = arrayOf(
        Migration1To2(),
        Migration2To3(),
        Migration3To4()
    )

    private class Migration1To2 : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE historical_draws
                ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }

    private class Migration2To3 : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS check_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    gameNumbers TEXT NOT NULL,
                    contestNumber INTEGER NOT NULL,
                    checkedAt TEXT NOT NULL,
                    hits INTEGER NOT NULL,
                    scoreCounts TEXT NOT NULL,
                    lastHitContest INTEGER,
                    lastHitScore INTEGER,
                    notes TEXT
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_check_history_checkedAt ON check_history(checkedAt DESC)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_check_history_contestNumber ON check_history(contestNumber)"
            )
        }
    }

    private class Migration3To4 : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS statistics_cache (
                    windowSize INTEGER NOT NULL PRIMARY KEY,
                    reportJson TEXT NOT NULL,
                    cachedAt INTEGER NOT NULL,
                    ttlMs INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_statistics_cache_cachedAt ON statistics_cache(cachedAt)"
            )
        }
    }
}

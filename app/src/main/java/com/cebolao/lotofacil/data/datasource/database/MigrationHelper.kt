package com.cebolao.lotofacil.data.datasource.database

import androidx.room.migration.Migration

/**
 * Baseline schema for development.
 *
 * App is currently unreleased, so Room starts at version 1 and does not need
 * historical migration chain yet.
 */
object MigrationHelper {
    fun getMigrations(): Array<Migration> = emptyArray()
}

package com.example.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS generation_jobs (id TEXT NOT NULL PRIMARY KEY, projectId TEXT NOT NULL, status TEXT NOT NULL, progress INTEGER NOT NULL, currentStep TEXT, attempt INTEGER NOT NULL, errorMessage TEXT, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_generation_jobs_projectId ON generation_jobs(projectId)")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE projects ADD COLUMN videoUri TEXT")
        }
    }
}

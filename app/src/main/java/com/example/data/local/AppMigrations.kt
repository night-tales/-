package com.example.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppMigrations {
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE generated_stories ADD COLUMN projectId TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "ALTER TABLE generated_stories ADD COLUMN ownerId TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_generated_stories_projectId ON generated_stories(projectId)"
            )

            db.execSQL(
                "ALTER TABLE sync_operations ADD COLUMN entityType TEXT NOT NULL DEFAULT 'PROJECT'"
            )
            db.execSQL(
                "ALTER TABLE sync_operations ADD COLUMN entityId TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "UPDATE sync_operations SET entityId = projectId WHERE entityId = ''"
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_sync_operations_entityType_entityId_operation ON sync_operations(entityType, entityId, operation)"
            )
        }
    }
}

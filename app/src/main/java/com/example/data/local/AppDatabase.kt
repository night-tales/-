package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.GeneratedStoryDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.dao.SyncOperationDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.GeneratedStoryEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.local.entity.SyncOperationEntity

@Database(
    entities = [
        ProjectEntity::class,
        SceneEntity::class,
        GeneratedStoryEntity::class,
        CharacterEntity::class,
        SyncOperationEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun generatedStoryDao(): GeneratedStoryDao
    abstract fun characterDao(): CharacterDao
    abstract fun syncOperationDao(): SyncOperationDao
}

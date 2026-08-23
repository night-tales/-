package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao

@Database(
    entities = [ProjectEntity::class, SceneEntity::class, com.example.data.local.entity.GeneratedStoryEntity::class, com.example.data.local.entity.CharacterEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun generatedStoryDao(): com.example.data.local.dao.GeneratedStoryDao
    abstract fun characterDao(): com.example.data.local.dao.CharacterDao
}

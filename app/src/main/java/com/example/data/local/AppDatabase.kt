package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.GenerationJobDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.GenerationJobEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity

@Database(
    entities = [ProjectEntity::class, SceneEntity::class, GenerationJobEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun generationJobDao(): GenerationJobDao
}

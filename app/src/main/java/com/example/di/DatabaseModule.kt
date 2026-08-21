package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseMigrations
import com.example.data.local.dao.GenerationJobDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "night_tales_db")
            .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3)
            .build()

    @Provides fun provideProjectDao(database: AppDatabase): ProjectDao = database.projectDao()
    @Provides fun provideSceneDao(database: AppDatabase): SceneDao = database.sceneDao()
    @Provides fun provideGenerationJobDao(database: AppDatabase): GenerationJobDao = database.generationJobDao()
}

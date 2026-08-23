package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.dao.ProjectDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "night_tales_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    fun provideSceneDao(database: AppDatabase): com.example.data.local.dao.SceneDao {
        return database.sceneDao()
    }

    @Provides
    fun provideGeneratedStoryDao(database: AppDatabase): com.example.data.local.dao.GeneratedStoryDao {
        return database.generatedStoryDao()
    }
}

package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.AppMigrations
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SyncOperationDao
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
        ).addMigrations(
            AppMigrations.MIGRATION_4_5,
            AppMigrations.MIGRATION_5_6,
            AppMigrations.MIGRATION_6_7
        ).build()
    }

    @Provides
    fun provideProjectDao(database: AppDatabase): ProjectDao = database.projectDao()

    @Provides
    fun provideSceneDao(database: AppDatabase): com.example.data.local.dao.SceneDao = database.sceneDao()

    @Provides
    fun provideSyncOperationDao(database: AppDatabase): SyncOperationDao = database.syncOperationDao()

    @Provides
    fun provideGeneratedStoryDao(database: AppDatabase): com.example.data.local.dao.GeneratedStoryDao = database.generatedStoryDao()

    @Provides
    fun provideCharacterDao(database: AppDatabase): com.example.data.local.dao.CharacterDao = database.characterDao()
}

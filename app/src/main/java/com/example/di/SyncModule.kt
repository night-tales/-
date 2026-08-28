package com.example.di

import com.example.data.remote.FirestoreProjectChildrenDataSource
import com.example.data.remote.FirestoreGeneratedStoryDataSource
import com.example.data.remote.FirestoreProjectDataSource
import com.example.data.remote.RemoteProjectDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    abstract fun bindRemoteProjectDataSource(
        source: FirestoreProjectDataSource
    ): RemoteProjectDataSource

    companion object {
        @Provides
        fun provideProjectChildrenDataSource(
            source: FirestoreProjectChildrenDataSource
        ): FirestoreProjectChildrenDataSource = source

        @Provides
        fun provideGeneratedStoryDataSource(
            source: FirestoreGeneratedStoryDataSource
        ): FirestoreGeneratedStoryDataSource = source
    }
}

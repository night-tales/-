package com.example.di

import com.example.data.remote.FirestoreProjectDataSource
import com.example.data.remote.RemoteProjectDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    abstract fun bindRemoteProjectDataSource(
        source: FirestoreProjectDataSource
    ): RemoteProjectDataSource
}

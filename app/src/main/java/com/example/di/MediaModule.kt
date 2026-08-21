package com.example.di

import com.example.generation.AudioProvider
import com.example.generation.ImageProvider
import com.example.generation.UnconfiguredAudioProvider
import com.example.generation.UnconfiguredImageProvider
import com.example.generation.UnconfiguredVideoAssembler
import com.example.generation.VideoAssembler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideImageProvider(): ImageProvider = UnconfiguredImageProvider()

    @Provides
    @Singleton
    fun provideAudioProvider(): AudioProvider = UnconfiguredAudioProvider()

    @Provides
    @Singleton
    fun provideVideoAssembler(): VideoAssembler = UnconfiguredVideoAssembler()
}

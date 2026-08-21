package com.example.di

import android.content.Context
import com.example.generation.AudioProvider
import com.example.generation.GeminiImageProvider
import com.example.generation.GeminiTtsAudioProvider
import com.example.generation.ImageProvider
import com.example.generation.Media3VideoAssembler
import com.example.generation.VideoAssembler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideImageProvider(@ApplicationContext context: Context): ImageProvider =
        GeminiImageProvider(context)

    @Provides
    @Singleton
    fun provideAudioProvider(@ApplicationContext context: Context): AudioProvider =
        GeminiTtsAudioProvider(context)

    @Provides
    @Singleton
    fun provideVideoAssembler(@ApplicationContext context: Context): VideoAssembler =
        Media3VideoAssembler(context)
}

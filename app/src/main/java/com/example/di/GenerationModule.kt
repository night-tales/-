package com.example.di

import com.example.generation.AudioProvider
import com.example.generation.ConfiguredMediaProviders
import com.example.generation.ImageProvider
import com.example.generation.VideoAssembler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GenerationModule {
    @Binds abstract fun bindImageProvider(provider: ConfiguredMediaProviders): ImageProvider
    @Binds abstract fun bindAudioProvider(provider: ConfiguredMediaProviders): AudioProvider
    @Binds abstract fun bindVideoAssembler(provider: ConfiguredMediaProviders): VideoAssembler
}

package com.example.di

import com.example.generation.GenerationPipeline
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GenerationEntryPoint {
    fun generationPipeline(): GenerationPipeline
}

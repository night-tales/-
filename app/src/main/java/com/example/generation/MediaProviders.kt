package com.example.generation

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

interface ImageProvider {
    suspend fun generate(prompt: String): Uri
}

interface AudioProvider {
    suspend fun synthesize(text: String): Uri
}

interface VideoAssembler {
    suspend fun assemble(sceneMedia: List<SceneMedia>): Uri
}

data class SceneMedia(val imageUri: Uri, val audioUri: Uri?, val durationMs: Long)

/** Explicitly fails until a real provider is configured; never creates demo media. */
class ConfiguredMediaProviders @Inject constructor(@ApplicationContext private val context: Context) : ImageProvider, AudioProvider, VideoAssembler {
    override suspend fun generate(prompt: String): Uri = throw IllegalStateException("Image provider is not configured")
    override suspend fun synthesize(text: String): Uri = throw IllegalStateException("Audio provider is not configured")
    override suspend fun assemble(sceneMedia: List<SceneMedia>): Uri {
        if (sceneMedia.isEmpty()) throw IllegalArgumentException("No scene media to assemble")
        throw IllegalStateException("Video assembler is not configured")
    }
}

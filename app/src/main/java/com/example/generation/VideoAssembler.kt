package com.example.generation

import android.net.Uri

data class SceneMedia(
    val imageUri: Uri,
    val audioUri: Uri?,
    val durationMs: Long
)

interface VideoAssembler {
    suspend fun assemble(scenes: List<SceneMedia>): Uri
}

package com.example.generation

import android.net.Uri

class UnconfiguredImageProvider : ImageProvider {
    override suspend fun generate(prompt: String): Uri =
        error("Image provider is not configured. Configure a production ImageProvider before generation.")
}

class UnconfiguredAudioProvider : AudioProvider {
    override suspend fun synthesize(text: String): Uri =
        error("Audio provider is not configured. Configure a production AudioProvider before generation.")
}

class UnconfiguredVideoAssembler : VideoAssembler {
    override suspend fun assemble(scenes: List<SceneMedia>): Uri =
        error("Video assembler is not configured. Configure a production VideoAssembler before generation.")
}

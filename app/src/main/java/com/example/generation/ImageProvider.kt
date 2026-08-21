package com.example.generation

import android.net.Uri

interface ImageProvider {
    suspend fun generate(prompt: String): Uri
}

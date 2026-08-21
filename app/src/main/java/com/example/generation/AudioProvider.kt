package com.example.generation

import android.net.Uri

interface AudioProvider {
    suspend fun synthesize(text: String): Uri
}

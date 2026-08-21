package com.example.generation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.ai.Firebase
import com.google.firebase.ai.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/** Production image adapter backed by Firebase AI Logic + Gemini 3.1 Flash Image. */
class GeminiImageProvider @Inject constructor(
    private val context: Context
) : ImageProvider {
    override suspend fun generate(prompt: String): Uri = withContext(Dispatchers.IO) {
        require(prompt.isNotBlank()) { "Image prompt must not be blank" }

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.1-flash-image",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )

        val response = model.generateContent(prompt)
        val bitmap = response.candidates.firstOrNull()
            ?.content
            ?.parts
            ?.filterIsInstance<com.google.firebase.ai.type.ImagePart>()
            ?.firstOrNull()
            ?.image
            ?: error("Gemini returned no image for the scene prompt")

        persist(bitmap)
    }

    private fun persist(bitmap: Bitmap): Uri {
        val directory = File(context.filesDir, "generated/scenes").apply { mkdirs() }
        val file = File(directory, "scene_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                "Failed to persist generated image"
            }
        }
        return Uri.fromFile(file)
    }
}

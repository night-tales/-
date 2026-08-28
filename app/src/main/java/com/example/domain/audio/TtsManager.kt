package com.example.domain.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private fun initTts(onInit: (Boolean) -> Unit) {
        if (isInitialized) {
            onInit(true)
            return
        }
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Arabic first
                val result = tts?.setLanguage(Locale("ar"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.getDefault())
                }
                isInitialized = true
                onInit(true)
            } else {
                isInitialized = false
                onInit(false)
            }
        }
    }

    suspend fun synthesizeToFile(text: String, outputFile: File): Result<File> = suspendCancellableCoroutine { continuation ->
        initTts { success ->
            if (!success || tts == null) {
                if (continuation.isActive) continuation.resume(Result.failure(Exception("TTS Initialization failed")))
                return@initTts
            }

            val utteranceId = "tts_${System.currentTimeMillis()}"
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(id: String?) {
                    if (id == utteranceId && continuation.isActive) {
                        continuation.resume(Result.success(outputFile))
                    }
                }

                @Deprecated("Deprecated in Java", ReplaceWith("onError(id, -1)"))
                override fun onError(id: String?) {
                    if (id == utteranceId && continuation.isActive) {
                        continuation.resume(Result.failure(Exception("TTS Synthesis error")))
                    }
                }
                
                override fun onError(id: String?, errorCode: Int) {
                    if (id == utteranceId && continuation.isActive) {
                        continuation.resume(Result.failure(Exception("TTS Synthesis error code: $errorCode")))
                    }
                }
            })

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            
            val status = tts?.synthesizeToFile(text, params, outputFile, utteranceId)
            
            if (status != TextToSpeech.SUCCESS) {
                if (continuation.isActive) continuation.resume(Result.failure(Exception("Failed to queue TTS synthesis")))
            }
        }
    }
}

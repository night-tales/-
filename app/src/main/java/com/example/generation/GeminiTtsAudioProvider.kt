package com.example.generation

import android.content.Context
import android.net.Uri
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Base64
import javax.inject.Inject

/** Gemini 3.1 Flash TTS adapter. The API returns 24 kHz mono PCM; we persist a WAV container. */
class GeminiTtsAudioProvider @Inject constructor(
    private val context: Context,
    private val client: OkHttpClient
) : AudioProvider {
    override suspend fun synthesize(text: String): Uri = withContext(Dispatchers.IO) {
        require(text.isNotBlank()) { "Narration text must not be blank" }
        require(BuildConfig.GEMINI_API_KEY.isNotBlank()) { "GEMINI_API_KEY is not configured" }

        val payload = JSONObject()
            .put("model", "gemini-3.1-flash-tts-preview")
            .put("input", text)
            .put("response_format", JSONObject().put("type", "audio"))
            .put(
                "generation_config",
                JSONObject().put(
                    "speech_config",
                    org.json.JSONArray().put(JSONObject().put("voice", "Kore"))
                )
            )

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/interactions")
            .header("x-goog-api-key", BuildConfig.GEMINI_API_KEY)
            .header("Content-Type", "application/json")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Gemini TTS failed (${response.code}): ${response.body?.string()?.take(500).orEmpty()}")
            }
            val json = JSONObject(response.body?.string().orEmpty())
            val encoded = json.optJSONObject("output_audio")?.optString("data").orEmpty()
            if (encoded.isBlank()) error("Gemini TTS returned no audio data")
            persistWav(Base64.getDecoder().decode(encoded))
        }
    }

    private fun persistWav(pcm: ByteArray): Uri {
        val directory = File(context.filesDir, "generated/audio").apply { mkdirs() }
        val file = File(directory, "narration_${System.currentTimeMillis()}.wav")
        FileOutputStream(file).use { output ->
            writeWavHeader(output, pcm.size, sampleRate = 24_000, channels = 1, bits = 16)
            output.write(pcm)
        }
        return Uri.fromFile(file)
    }

    private fun writeWavHeader(output: FileOutputStream, dataLength: Int, sampleRate: Int, channels: Int, bits: Int) {
        val byteRate = sampleRate * channels * bits / 8
        val blockAlign = channels * bits / 8
        val chunkSize = 36 + dataLength
        fun writeInt(value: Int) = output.write(byteArrayOf(
            value.toByte(), (value shr 8).toByte(), (value shr 16).toByte(), (value shr 24).toByte()
        ).toIntArray())
        fun writeShort(value: Int) = output.write(byteArrayOf(value.toByte(), (value shr 8).toByte()).toIntArray())
        output.write("RIFF".toByteArray()); writeInt(chunkSize); output.write("WAVE".toByteArray())
        output.write("fmt ".toByteArray()); writeInt(16); writeShort(1); writeShort(channels)
        writeInt(sampleRate); writeInt(byteRate); writeShort(blockAlign); writeShort(bits)
        output.write("data".toByteArray()); writeInt(dataLength)
    }
}

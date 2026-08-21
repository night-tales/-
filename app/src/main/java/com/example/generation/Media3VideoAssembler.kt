package com.example.generation

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import javax.inject.Inject

/**
 * Assembles generated scene images and narration tracks into a playable MP4.
 * Media3 Transformer supports multi-asset Composition export on-device.
 */
@OptIn(UnstableApi::class)
class Media3VideoAssembler @Inject constructor(
    private val context: Context
) : VideoAssembler {
    override suspend fun assemble(scenes: List<SceneMedia>): Uri = withContext(Dispatchers.Main.immediate) {
        require(scenes.isNotEmpty()) { "Cannot assemble an empty scene list" }
        require(scenes.all { it.audioUri != null }) { "Every scene requires narration audio" }

        val videoItems = scenes.map { scene ->
            val mediaItem = MediaItem.Builder()
                .setUri(scene.imageUri)
                .setImageDurationMs(scene.durationMs.coerceAtLeast(1_000L))
                .build()
            EditedMediaItem.Builder(mediaItem)
                .setFrameRate(30)
                .build()
        }
        val audioItems = scenes.map { scene ->
            EditedMediaItem.Builder(MediaItem.fromUri(scene.audioUri!!)).build()
        }

        val videoSequence = EditedMediaItemSequence.withVideoFrom(videoItems)
        val audioSequence = EditedMediaItemSequence.withAudioFrom(audioItems)
        val composition = Composition.Builder(videoSequence, audioSequence).build()

        val output = File(context.filesDir, "generated/final").apply { mkdirs() }
            .resolve("night_tales_${System.currentTimeMillis()}.mp4")

        suspendCancellableCoroutine { continuation ->
            val transformer = Transformer.Builder(context)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: androidx.media3.transformer.ExportResult) {
                        if (continuation.isActive) continuation.resume(Uri.fromFile(output))
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: androidx.media3.transformer.ExportResult,
                        exportException: androidx.media3.transformer.ExportException
                    ) {
                        if (continuation.isActive) continuation.resumeWithException(exportException)
                    }
                })
                .build()

            continuation.invokeOnCancellation { transformer.cancel() }
            transformer.start(composition, output.absolutePath)
        }
    }
}

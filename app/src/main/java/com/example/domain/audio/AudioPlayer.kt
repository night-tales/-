package com.example.domain.audio

import android.media.MediaPlayer
import java.io.IOException

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    
    fun play(filePath: String, onCompletion: () -> Unit) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener { 
                    onCompletion()
                    stop()
                }
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    
    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

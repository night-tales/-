package com.example.studio.image

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {
    private val _prompt = MutableStateFlow("")
    val prompt = _prompt.asStateFlow()

    private val _negativePrompt = MutableStateFlow("ugly, blurry, low quality")
    val negativePrompt = _negativePrompt.asStateFlow()

    private val _style = MutableStateFlow("Cinematic Fantasy")
    val style = _style.asStateFlow()
    
    private val _aspectRatio = MutableStateFlow("16:9")
    val aspectRatio = _aspectRatio.asStateFlow()

    fun updatePrompt(value: String) { _prompt.value = value }
    fun updateNegativePrompt(value: String) { _negativePrompt.value = value }
    fun updateStyle(value: String) { _style.value = value }
    fun updateAspectRatio(value: String) { _aspectRatio.value = value }
}

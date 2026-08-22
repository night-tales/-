package com.example.studio.export

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor() : ViewModel() {
    private val _quality = MutableStateFlow("1080p")
    val quality = _quality.asStateFlow()

    private val _fps = MutableStateFlow(30)
    val fps = _fps.asStateFlow()

    private val _aspectRatio = MutableStateFlow("16:9")
    val aspectRatio = _aspectRatio.asStateFlow()

    private val _isRendering = MutableStateFlow(false)
    val isRendering = _isRendering.asStateFlow()

    private val _renderProgress = MutableStateFlow(0f)
    val renderProgress = _renderProgress.asStateFlow()

    fun updateQuality(value: String) { _quality.value = value }
    fun updateFps(value: Int) { _fps.value = value }
    fun updateAspectRatio(value: String) { _aspectRatio.value = value }

    fun startRender() {
        _isRendering.value = true
        _renderProgress.value = 0.82f // Mock progress
    }
}

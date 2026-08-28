package com.example.studio.scene

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.SceneEntity
import com.example.domain.ai.AiDirector
import com.example.domain.audio.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SceneViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val sceneDao: SceneDao,
    private val ttsManager: TtsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val currentProjectId = "proj_1"

    val scenes: StateFlow<List<SceneEntity>> = sceneDao.getScenesForProject(currentProjectId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun generateImage(sceneId: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val scene = sceneDao.getSceneById(sceneId)
            if (scene != null && scene.imagePrompt != null) {
                val imageUrl = aiDirector.generateImageForScene(scene.imagePrompt + ", highly detailed cinematic shot, film still")
                sceneDao.updateScene(scene.copy(imageUrl = imageUrl))
            }
            _isGenerating.value = false
        }
    }

    fun generateVoiceOver(sceneId: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val scene = sceneDao.getSceneById(sceneId)
            if (scene != null && scene.narration.isNotBlank()) {
                val outputDir = File(context.cacheDir, "audio")
                if (!outputDir.exists()) outputDir.mkdirs()
                val outputFile = File(outputDir, "scene_${scene.id}.wav")
                
                val result = ttsManager.synthesizeToFile(scene.narration, outputFile)
                if (result.isSuccess) {
                    sceneDao.updateScene(scene.copy(audioUrl = outputFile.absolutePath))
                }
            }
            _isGenerating.value = false
        }
    }

    fun addNewScene() {
        viewModelScope.launch {
            val newScene = SceneEntity(
                id = UUID.randomUUID().toString(),
                projectId = currentProjectId,
                index = scenes.value.size,
                title = "مشهد جديد",
                narration = "في إحدى الليالي المقمرة، كان آدم يسير في الغابة...",
                imagePrompt = "A cinematic view...",
                durationMs = 5000L
            )
            sceneDao.insertScene(newScene)
        }
    }
}

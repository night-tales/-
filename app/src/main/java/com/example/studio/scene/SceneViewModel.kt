package com.example.studio.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.SceneEntity
import com.example.domain.ai.AiDirector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SceneViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val sceneDao: SceneDao
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

    fun addNewScene() {
        viewModelScope.launch {
            val newScene = SceneEntity(
                id = UUID.randomUUID().toString(),
                projectId = currentProjectId,
                index = scenes.value.size,
                title = "مشهد جديد",
                narration = "انقر هنا لتعديل نص السرد الخاص بهذا المشهد...",
                imagePrompt = "A cinematic view...",
                durationMs = 5000L
            )
            sceneDao.insertScene(newScene)
        }
    }
}

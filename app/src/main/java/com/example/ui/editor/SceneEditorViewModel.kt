package com.example.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.SceneEntity
import com.example.data.repository.ProjectRepository
import com.example.domain.ai.AiDirector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SceneEditorViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val aiDirector: AiDirector,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _scenes = MutableStateFlow<List<SceneEntity>>(emptyList())
    val scenes: StateFlow<List<SceneEntity>> = _scenes.asStateFlow()

    private val currentProjectId: String? = savedStateHandle.get<String>("projectId")

    init {
        val projectId = currentProjectId ?: return
        viewModelScope.launch {
            repository.getScenesForProject(projectId).collect { dbScenes ->
                _scenes.value = dbScenes
            }
        }
    }

    fun updateSceneText(sceneId: String, newText: String) {
        val updatedScenes = _scenes.value.map {
            if (it.id == sceneId) it.copy(narration = newText) else it
        }
        _scenes.value = updatedScenes
        autoSave(updatedScenes)
    }

    fun updateSceneImagePrompt(sceneId: String, newPrompt: String) {
        val updatedScenes = _scenes.value.map {
            if (it.id == sceneId) it.copy(imagePrompt = newPrompt) else it
        }
        _scenes.value = updatedScenes
        autoSave(updatedScenes)
    }

    fun generateImageForScene(sceneId: String, prompt: String) {
        viewModelScope.launch {
            try {
                val url = aiDirector.generateImageForScene(prompt)
                val updatedScenes = _scenes.value.map {
                    if (it.id == sceneId) it.copy(imageUrl = url) else it
                }
                _scenes.value = updatedScenes
                autoSave(updatedScenes)
            } catch (_: Exception) {
                // UI remains unchanged; the generation pipeline will expose errors explicitly.
            }
        }
    }

    private fun autoSave(scenesToSave: List<SceneEntity>) {
        if (currentProjectId == null || scenesToSave.isEmpty()) return
        viewModelScope.launch {
            repository.saveScenes(scenesToSave)
        }
    }
}

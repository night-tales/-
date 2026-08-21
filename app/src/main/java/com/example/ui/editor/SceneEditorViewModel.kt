package com.example.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SceneEditorViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _scenes = MutableStateFlow<List<SceneEntity>>(emptyList())
    val scenes: StateFlow<List<SceneEntity>> = _scenes.asStateFlow()
    
    // For prototyping, we use a hardcoded project ID unless one is passed
    private val currentProjectId: String = "project_1"

    init {
        viewModelScope.launch {
            // Ensure project exists
            var project = repository.getProjectById(currentProjectId)
            if (project == null) {
                project = ProjectEntity(
                    id = currentProjectId,
                    title = "مشروع جديد",
                    genre = "مغامرة",
                    durationMinutes = 5,
                    status = "DRAFT",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveProject(project)
            }

            repository.getScenesForProject(currentProjectId).collect { dbScenes ->
                if (dbScenes.isEmpty()) {
                    val initialScenes = listOf(
                        SceneEntity("scene_1", currentProjectId, 0, "المشهد الأول", "في ليلة هادئة، كانت الرياح تعصف بشدة...", "صورة ليلية عاصفة لقلعة قديمة"),
                        SceneEntity("scene_2", currentProjectId, 1, "المشهد الثاني", "فجأة، ظهر ضوء غامض من بعيد...", "ضوء متوهج في غابة مظلمة"),
                        SceneEntity("scene_3", currentProjectId, 2, "المشهد الثالث", "اقترب البطل بحذر ليرى ما يختبئ في الظلام...", "شخص يقف أمام كهف مضاء")
                    )
                    repository.saveScenes(initialScenes)
                } else {
                    _scenes.value = dbScenes
                }
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
    
    private fun autoSave(scenesToSave: List<SceneEntity>) {
        viewModelScope.launch {
            repository.saveScenes(scenesToSave)
        }
    }
}

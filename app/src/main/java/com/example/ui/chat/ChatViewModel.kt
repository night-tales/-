package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.repository.GenerationRepository
import com.example.data.repository.ProjectRepository
import com.example.domain.ai.AiAction
import com.example.domain.ai.AiDirector
import com.example.generation.GenerationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val projectRepository: ProjectRepository,
    private val generationRepository: GenerationRepository,
    private val generationScheduler: GenerationScheduler
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _blueprint = MutableStateFlow<BlueprintUi?>(null)
    val blueprint: StateFlow<BlueprintUi?> = _blueprint.asStateFlow()

    private val _characterReference = MutableStateFlow<CharacterReferenceUi?>(null)
    val characterReference: StateFlow<CharacterReferenceUi?> = _characterReference.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _createdProjectId = MutableStateFlow<String?>(null)
    val createdProjectId: StateFlow<String?> = _createdProjectId.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        addMessage(ChatMessage(UUID.randomUUID().toString(), ChatRole.USER, text))

        viewModelScope.launch {
            addMessage(ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, "", status = ChatStatus.THINKING))
            val intent = aiDirector.understandIntent(text)
            val replyText = aiDirector.generateChatReply(text)
            removeLastAssistantMessage()
            addMessage(ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, replyText, status = ChatStatus.COMPLETE))

            if (intent is AiAction.CreateStory) {
                val generatedBlueprint = aiDirector.generateProjectBlueprint(text) ?: return@launch
                val projectId = UUID.randomUUID().toString()
                _createdProjectId.value = projectId
                val now = System.currentTimeMillis()
                val project = ProjectEntity(
                    id = projectId,
                    title = generatedBlueprint.title,
                    genre = generatedBlueprint.genre,
                    durationMinutes = generatedBlueprint.duration,
                    status = "DRAFT",
                    createdAt = now,
                    updatedAt = now
                )
                val scenes = (1..generatedBlueprint.scenesCount).map { i ->
                    SceneEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        index = i - 1,
                        title = "المشهد $i",
                        narration = "",
                        imagePrompt = "",
                        durationMs = 15000L
                    )
                }
                projectRepository.saveProject(project)
                projectRepository.saveScenes(scenes)
                _blueprint.value = BlueprintUi(
                    title = generatedBlueprint.title,
                    category = generatedBlueprint.genre,
                    duration = generatedBlueprint.duration,
                    hero = generatedBlueprint.heroName,
                    style = "سينمائي",
                    format = "16:9",
                    scenes = generatedBlueprint.scenesCount
                )
                _characterReference.value = CharacterReferenceUi(
                    id = "char_$projectId",
                    name = generatedBlueprint.heroName,
                    role = generatedBlueprint.heroRole,
                    description = generatedBlueprint.heroDescription,
                    style = "سينمائي واقعي",
                    imageUrl = null
                )
            }
        }
    }

    fun startGeneration() {
        _createdProjectId.value?.let { projectId ->
            viewModelScope.launch {
                val project = projectRepository.getProjectById(projectId) ?: return@launch
                val job = generationRepository.createJob(projectId)
                projectRepository.saveProject(project.copy(status = "QUEUED"))
                generationScheduler.enqueue(job)
                _isGenerating.value = true
                _progress.value = 0f
            }
        }
    }

    fun stopGeneration() {
        _createdProjectId.value?.let { projectId ->
            generationScheduler.cancel(projectId)
            viewModelScope.launch {
                val project = projectRepository.getProjectById(projectId)
                if (project != null) projectRepository.saveProject(project.copy(status = "CANCELLED"))
            }
        }
        _isGenerating.value = false
    }

    fun retry() = startGeneration()

    fun newChat() {
        _messages.value = emptyList()
        _blueprint.value = null
        _characterReference.value = null
        _createdProjectId.value = null
        _isGenerating.value = false
        _progress.value = 0f
    }

    private fun addMessage(message: ChatMessage) { _messages.update { it + message } }

    private fun removeLastAssistantMessage() {
        _messages.update { list -> list.dropLastWhile { it.role == ChatRole.ASSISTANT } }
    }
}

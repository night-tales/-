package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

import com.example.domain.ai.AiAction
import com.example.domain.ai.AiDirector
import com.example.data.repository.ProjectRepository
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val projectRepository: ProjectRepository
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

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = ChatRole.USER,
                text = text
            )
        )

        viewModelScope.launch {
            addMessage(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = ChatRole.ASSISTANT,
                    text = "",
                    status = ChatStatus.THINKING
                )
            )

            // Step 1: Detect intent and get text reply in parallel (conceptually, or sequential)
            val intent = aiDirector.understandIntent(text)
            val replyText = aiDirector.generateChatReply(text)

            removeLastAssistantMessage()

            addMessage(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = ChatRole.ASSISTANT,
                    text = replyText,
                    status = ChatStatus.COMPLETE
                )
            )

            // Step 2: If intent is to create a story, trigger the Blueprint generation.
            if (intent is AiAction.CreateStory) {
                // Call AiDirector to generate a blueprint based on the user's idea
                val generatedBlueprint = aiDirector.generateProjectBlueprint(text)

                if (generatedBlueprint != null) {
                    val projectId = UUID.randomUUID().toString()
                    _createdProjectId.value = projectId
                    
                    val project = ProjectEntity(
                        id = projectId,
                        title = generatedBlueprint.title,
                        genre = generatedBlueprint.genre,
                        durationMinutes = generatedBlueprint.duration,
                        status = "DRAFT",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    val scenes = (1..generatedBlueprint.scenesCount).map { i ->
                        SceneEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = projectId,
                            index = i - 1,
                            title = "المشهد $i",
                            narration = "جاري كتابة المحتوى لهذا المشهد...",
                            imagePrompt = "مشهد مقترح لقصة ${generatedBlueprint.title}",
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
                        style = "سينمائي", // Defaulting style for now
                        format = "16:9", // Defaulting format
                        scenes = generatedBlueprint.scenesCount
                    )

                    // Simulate Character Reference generated based on the blueprint
                    _characterReference.value = CharacterReferenceUi(
                        id = "char_generated_${UUID.randomUUID()}",
                        name = generatedBlueprint.heroName,
                        role = generatedBlueprint.heroRole,
                        description = generatedBlueprint.heroDescription,
                        style = "سينمائي واقعي", // default style matching blueprint
                        imageUrl = "dummy_url" // In a real flow, this would call Image generation API
                    )
                } else {
                     // Fallback if AI fails to return structured JSON
                    _blueprint.value = BlueprintUi(
                        title = "مغامرة جديدة",
                        category = "مغامرة • خيال",
                        duration = 5,
                        hero = "شخصية رئيسية",
                        style = "سينمائي",
                        format = "16:9",
                        scenes = 8
                    )
                    _characterReference.value = CharacterReferenceUi(
                        id = "char1",
                        name = "البطل",
                        role = "الشخصية الرئيسية",
                        description = "تم توليد الوصف كبديل للخطأ.",
                        style = "سينمائي واقعي",
                        imageUrl = "dummy_url"
                    )
                }
            }
        }
    }

    fun startGeneration() {
        viewModelScope.launch {
            _isGenerating.value = true
            _progress.value = 0f
            
            // Simulate generation progress
            for (i in 1..100) {
                delay(50)
                _progress.value = i / 100f
            }
            
            _isGenerating.value = false
        }
    }

    fun stopGeneration() {
        _isGenerating.value = false
    }

    fun retry() {
        // Retry logic
    }

    fun newChat() {
        _messages.value = emptyList()
        _blueprint.value = null
        _isGenerating.value = false
        _progress.value = 0f
    }

    private fun addMessage(message: ChatMessage) {
        _messages.update { it + message }
    }

    private fun removeLastAssistantMessage() {
        _messages.update { list ->
            list.dropLastWhile { it.role == ChatRole.ASSISTANT }
        }
    }
}

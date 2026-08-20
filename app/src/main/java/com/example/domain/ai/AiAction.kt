package com.example.domain.ai

sealed interface AiAction {
    data class CreateStory(val prompt: String) : AiAction
    data class ModifyStory(val instruction: String) : AiAction
    data class CreateImage(val prompt: String) : AiAction
    data class CreateAudio(val text: String) : AiAction
    data class CreateVideo(val projectId: String) : AiAction
    data object Unknown : AiAction
}

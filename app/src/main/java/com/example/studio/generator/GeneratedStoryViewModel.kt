package com.example.studio.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.GeneratedStoryDao
import com.example.data.local.entity.GeneratedStoryEntity
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
class GeneratedStoryViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val generatedStoryDao: GeneratedStoryDao
) : ViewModel() {

    val stories: StateFlow<List<GeneratedStoryEntity>> = generatedStoryDao.getAllStories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteStories: StateFlow<List<GeneratedStoryEntity>> = generatedStoryDao.getFavoriteStories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generatedStoryId = MutableStateFlow<String?>(null)
    val generatedStoryId = _generatedStoryId.asStateFlow()

    private val _currentStory = MutableStateFlow<GeneratedStoryEntity?>(null)
    val currentStory = _currentStory.asStateFlow()

    fun generateStory(prompt: String, title: String, category: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val content = aiDirector.generateStoryText(prompt)
            val newStory = GeneratedStoryEntity(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                title = title.ifBlank { "قصة جديدة" },
                content = content,
                createdAt = System.currentTimeMillis(),
                category = category,
                isFavorite = false
            )
            generatedStoryDao.insertStory(newStory)
            _generatedStoryId.value = newStory.id
            _isGenerating.value = false
        }
    }

    fun loadStory(id: String) {
        viewModelScope.launch {
            _currentStory.value = generatedStoryDao.getStoryById(id)
        }
    }

    fun toggleFavorite(story: GeneratedStoryEntity) {
        viewModelScope.launch {
            val updatedStory = story.copy(isFavorite = !story.isFavorite)
            generatedStoryDao.updateStory(updatedStory)
            if (_currentStory.value?.id == story.id) {
                _currentStory.value = updatedStory
            }
        }
    }

    fun resetNavigation() {
        _generatedStoryId.value = null
    }
}

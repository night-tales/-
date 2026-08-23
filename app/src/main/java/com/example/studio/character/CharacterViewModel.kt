package com.example.studio.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.CharacterDao
import com.example.data.local.entity.CharacterEntity
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
class CharacterViewModel @Inject constructor(
    private val aiDirector: AiDirector,
    private val characterDao: CharacterDao
) : ViewModel() {

    // Hardcoding a default project ID for now, as we don't have a project context passed yet
    private val currentProjectId = "proj_1"

    val characters: StateFlow<List<CharacterEntity>> = characterDao.getCharactersForProject(currentProjectId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun generateCharacter(prompt: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val blueprint = aiDirector.generateCharacterBlueprint(prompt)
            if (blueprint != null) {
                // Generate a portrait image based on visual prompt
                val imageUrl = aiDirector.generateImageForScene(blueprint.visualPrompt + ", portrait, concept art, highly detailed, expressive")
                
                val newChar = CharacterEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = currentProjectId,
                    name = blueprint.name,
                    description = blueprint.description,
                    visualPrompt = blueprint.visualPrompt,
                    referenceImagePath = imageUrl,
                    traits = blueprint.traits
                )
                characterDao.insertCharacter(newChar)
            }
            _isGenerating.value = false
        }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            characterDao.deleteCharacter(character.id)
        }
    }
}

package com.example.studio.character

import androidx.lifecycle.ViewModel
import com.example.domain.model.CharacterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor() : ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterInfo>>(
        listOf(
            CharacterInfo("1", "آدم", "البطل", 11, "فضولي وشجاع، يرتدي سترة حمراء ويحمل دائماً حقيبة ظهر صغيرة."),
            CharacterInfo("2", "لينا", "شخصية مساعدة", 12, "ذكية وهادئة، لديها نظارات دائرية وتحب قراءة الكتب القديمة.")
        )
    )
    val characters = _characters.asStateFlow()

    fun addCharacter() {
        // Placeholder for adding new
    }
}

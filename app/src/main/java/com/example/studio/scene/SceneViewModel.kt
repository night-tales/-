package com.example.studio.scene

import androidx.lifecycle.ViewModel
import com.example.domain.model.SceneInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SceneViewModel @Inject constructor() : ViewModel() {
    private val _scenes = MutableStateFlow<List<SceneInfo>>(
        listOf(
            SceneInfo(
                id = "s1",
                order = 1,
                title = "الباب الغامض",
                description = "آدم يلاحظ ضوءًا أزرق خلف خزانته.",
                durationSeconds = 32,
                charactersIds = listOf("1"),
                cameraDirection = "Slow Push In",
                action = "Adam approaches the door",
                soundEnv = "Wind + room ambience",
                dialog = "ما هذا؟"
            )
        )
    )
    val scenes = _scenes.asStateFlow()
}

package com.example.domain.model

data class SceneInfo(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
    val durationSeconds: Int,
    val charactersIds: List<String>,
    val cameraDirection: String,
    val action: String,
    val soundEnv: String,
    val dialog: String?,
    val imageUrl: String? = null
)

package com.example.domain.model

data class Project(
    val id: String,
    val title: String,
    val genre: String,
    val durationMinutes: Int,
    val progress: Float,
    val coverImageUrl: String?,
    val status: ProjectStatus
)

data class ProjectStatus(
    val storyReady: Boolean = false,
    val charactersReady: Boolean = false,
    val scenesReady: Boolean = false,
    val imagesReady: Boolean = false,
    val voiceReady: Boolean = false,
    val musicReady: Boolean = false,
    val subtitlesReady: Boolean = false,
    val timelineReady: Boolean = false
)

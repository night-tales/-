package com.example.data.local.entity





data class GenerationJobEntity(
    val id: String,
    val projectId: String,
    val status: GenerationStatus,
    val progress: Int,
    val currentStep: String,
    val error: String? = null
)

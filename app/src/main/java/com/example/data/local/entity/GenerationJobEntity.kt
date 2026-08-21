package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generation_jobs")
data class GenerationJobEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val status: String,
    val progress: Int = 0,
    val currentStep: String? = null,
    val attempt: Int = 0,
    val errorMessage: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

object GenerationStatus {
    const val QUEUED = "QUEUED"
    const val GENERATING = "GENERATING"
    const val COMPLETED = "COMPLETED"
    const val FAILED = "FAILED"
    const val CANCELLED = "CANCELLED"
}

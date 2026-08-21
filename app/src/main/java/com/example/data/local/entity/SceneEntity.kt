package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val index: Int,
    val title: String,
    val narration: String,
    val imagePrompt: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val durationMs: Long = 0
)

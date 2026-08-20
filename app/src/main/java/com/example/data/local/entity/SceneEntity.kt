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
    val imageUrl: String?,
    val audioUrl: String?,
    val durationMs: Long
)

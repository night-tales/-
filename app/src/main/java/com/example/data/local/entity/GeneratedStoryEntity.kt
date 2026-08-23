package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_stories")
data class GeneratedStoryEntity(
    @PrimaryKey val id: String,
    val prompt: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val category: String = "عام",
    val isFavorite: Boolean = false
)

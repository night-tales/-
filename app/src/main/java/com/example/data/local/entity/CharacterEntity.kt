package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val description: String,
    val visualPrompt: String,
    val referenceImagePath: String?,
    val traits: String,
    val createdAt: Long = System.currentTimeMillis()
)

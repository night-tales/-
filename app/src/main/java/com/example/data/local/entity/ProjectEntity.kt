package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val ownerId: String = "",
    val title: String,
    val genre: String,
    val durationMinutes: Int,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)

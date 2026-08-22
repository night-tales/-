package com.example.domain.model

data class CharacterInfo(
    val id: String,
    val name: String,
    val role: String,
    val age: Int,
    val description: String,
    val imageUrl: String? = null
)

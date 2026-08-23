package com.example.data.repository

import com.example.data.local.entity.GenerationStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerationRepository @Inject constructor() {
    suspend fun updateState(id: String, status: GenerationStatus, progress: Int, step: String, error: String? = null) {
        // TODO: Implement actual state updates, likely updating the DB or state flow.
    }
}

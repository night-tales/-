package com.example.data.repository

import com.example.data.local.dao.GenerationJobDao
import com.example.data.local.entity.GenerationJobEntity
import com.example.data.local.entity.GenerationStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GenerationRepository @Inject constructor(
    private val generationJobDao: GenerationJobDao
) {
    fun observeJobs(projectId: String): Flow<List<GenerationJobEntity>> = generationJobDao.getForProject(projectId)

    suspend fun createJob(projectId: String): GenerationJobEntity {
        val now = System.currentTimeMillis()
        return GenerationJobEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            status = GenerationStatus.QUEUED,
            createdAt = now,
            updatedAt = now
        ).also { generationJobDao.upsert(it) }
    }

    suspend fun cancelLatest(projectId: String) {
        generationJobDao.getLatestForProject(projectId)?.let {
            generationJobDao.updateState(
                it.id, GenerationStatus.CANCELLED, it.progress, "CANCELLED", null,
                System.currentTimeMillis()
            )
        }
    }

    suspend fun updateState(jobId: String, status: String, progress: Int, step: String? = null, errorMessage: String? = null) {
        generationJobDao.updateState(
            jobId, status, progress.coerceIn(0, 100), step, errorMessage, System.currentTimeMillis()
        )
    }
}

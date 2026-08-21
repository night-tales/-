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
    fun observeJobs(projectId: String): Flow<List<GenerationJobEntity>> =
        generationJobDao.getForProject(projectId)

    suspend fun createJob(projectId: String): GenerationJobEntity {
        val now = System.currentTimeMillis()
        val job = GenerationJobEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            status = GenerationStatus.QUEUED,
            createdAt = now,
            updatedAt = now
        )
        generationJobDao.upsert(job)
        return job
    }

    suspend fun updateState(
        jobId: String,
        status: String,
        progress: Int,
        step: String? = null,
        errorMessage: String? = null
    ) {
        generationJobDao.updateState(
            id = jobId,
            status = status,
            progress = progress.coerceIn(0, 100),
            step = step,
            errorMessage = errorMessage,
            updatedAt = System.currentTimeMillis()
        )
    }
}

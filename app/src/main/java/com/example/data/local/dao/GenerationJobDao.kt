package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.GenerationJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationJobDao {
    @Query("SELECT * FROM generation_jobs WHERE id = :id")
    suspend fun getById(id: String): GenerationJobEntity?

    @Query("SELECT * FROM generation_jobs WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getForProject(projectId: String): Flow<List<GenerationJobEntity>>

    @Query("SELECT * FROM generation_jobs WHERE projectId = :projectId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestForProject(projectId: String): GenerationJobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(job: GenerationJobEntity)

    @Query("UPDATE generation_jobs SET status = :status, progress = :progress, currentStep = :step, errorMessage = :errorMessage, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateState(id: String, status: String, progress: Int, step: String?, errorMessage: String?, updatedAt: Long)

    @Query("UPDATE generation_jobs SET attempt = attempt + 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun incrementAttempt(id: String, updatedAt: Long)

    @Query("DELETE FROM generation_jobs WHERE projectId = :projectId")
    suspend fun deleteForProject(projectId: String)
}

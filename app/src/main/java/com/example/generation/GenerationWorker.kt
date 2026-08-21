package com.example.generation

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseMigrations
import com.example.data.local.entity.GenerationStatus

class GenerationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.failure()
        val projectId = inputData.getString(KEY_PROJECT_ID) ?: return Result.failure()
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "night_tales_db")
            .addMigrations(DatabaseMigrations.MIGRATION_1_2)
            .build()
        return try {
            val jobDao = db.generationJobDao()
            val projectDao = db.projectDao()
            val job = jobDao.getById(jobId) ?: return Result.failure()
            if (job.status == GenerationStatus.CANCELLED) return Result.success()

            jobDao.updateState(jobId, GenerationStatus.GENERATING, 0, "PREPARING", null, System.currentTimeMillis())
            projectDao.updateStatus(projectId, GenerationStatus.GENERATING, System.currentTimeMillis())
            jobDao.updateState(jobId, GenerationStatus.GENERATING, 10, "PIPELINE_READY", null, System.currentTimeMillis())

            if (isStopped) return Result.retry()
            Result.success()
        } catch (t: Throwable) {
            jobDaoSafe(db, jobId, t)
            Result.failure()
        } finally {
            db.close()
        }
    }

    private suspend fun jobDaoSafe(db: AppDatabase, jobId: String, t: Throwable) {
        db.generationJobDao().updateState(jobId, GenerationStatus.FAILED, 0, "FAILED", t.message?.take(500), System.currentTimeMillis())
    }

    companion object {
        const val KEY_JOB_ID = "generation_job_id"
        const val KEY_PROJECT_ID = "project_id"
        const val UNIQUE_WORK_PREFIX = "night_tales_generation_"
    }
}

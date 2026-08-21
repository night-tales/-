package com.example.generation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.entity.GenerationStatus

class GenerationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.failure()
        val projectId = inputData.getString(KEY_PROJECT_ID) ?: return Result.failure()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "night_tales_db"
        ).build()

        return try {
            val jobDao = db.generationJobDao()
            val projectDao = db.projectDao()
            val job = jobDao.getById(jobId) ?: return Result.failure()

            if (job.status == GenerationStatus.CANCELLED) return Result.failure()

            jobDao.updateState(
                jobId, GenerationStatus.GENERATING, 0, "PREPARING", null,
                System.currentTimeMillis()
            )
            projectDao.updateStatus(projectId, GenerationStatus.GENERATING, System.currentTimeMillis())

            // This worker intentionally owns orchestration state only for now.
            // Real AI/media stages will be added without reintroducing fake UI progress.
            jobDao.updateState(
                jobId, GenerationStatus.GENERATING, 10, "QUEUED_PIPELINE", null,
                System.currentTimeMillis()
            )

            if (isStopped) return Result.retry()

            Result.success()
        } catch (t: Throwable) {
            db.generationJobDao().updateState(
                jobId,
                GenerationStatus.FAILED,
                0,
                "FAILED",
                t.message?.take(500),
                System.currentTimeMillis()
            )
            Result.failure()
        } finally {
            db.close()
        }
    }

    companion object {
        const val KEY_JOB_ID = "generation_job_id"
        const val KEY_PROJECT_ID = "project_id"
        const val UNIQUE_WORK_PREFIX = "night_tales_generation_"
    }
}

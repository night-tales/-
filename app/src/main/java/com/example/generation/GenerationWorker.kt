package com.example.generation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.entity.GenerationStatus
import com.example.di.GenerationEntryPoint
import dagger.hilt.android.EntryPointAccessors

class GenerationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.failure()
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, GenerationEntryPoint::class.java)
        val repository = entryPoint.generationRepository()
        val pipeline = entryPoint.generationPipeline()
        val job = repository.getJob(jobId) ?: return Result.failure()
        if (job.status == GenerationStatus.CANCELLED) return Result.success()
        if (isStopped) return Result.retry()
        return try {
            pipeline.run(job)
            Result.success()
        } catch (t: Throwable) {
            if (t is kotlinx.coroutines.CancellationException) throw t
            Result.failure()
        }
    }

    companion object {
        const val KEY_JOB_ID = "generation_job_id"
        const val KEY_PROJECT_ID = "project_id"
        const val UNIQUE_WORK_PREFIX = "night_tales_generation_"
    }
}

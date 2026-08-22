package com.example.generation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.entity.GenerationStatus
import com.example.di.GenerationEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.io.IOException
import kotlinx.coroutines.CancellationException

class GenerationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.failure()
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, GenerationEntryPoint::class.java)
        val repository = entryPoint.generationRepository()
        val pipeline = entryPoint.generationPipeline()
        val job = repository.getJob(jobId) ?: return Result.failure()

        if (job.status == GenerationStatus.CANCELLED) return Result.success()
        if (isStopped) return Result.retry()

        repository.incrementAttempt(jobId)
        return try {
            pipeline.run(job)
            Result.success()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (io: IOException) {
            if (runAttemptCount + 1 < MAX_ATTEMPTS) Result.retry() else Result.failure()
        } catch (t: Throwable) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_JOB_ID = "generation_job_id"
        const val KEY_PROJECT_ID = "project_id"
        const val UNIQUE_WORK_PREFIX = "night_tales_generation_"
        const val MAX_ATTEMPTS = 3
    }
}

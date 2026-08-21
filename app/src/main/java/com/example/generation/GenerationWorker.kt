package com.example.generation

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.example.data.repository.GenerationRepository
import com.example.data.local.entity.GenerationStatus

@HiltWorker
class GenerationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val generationRepository: GenerationRepository,
    private val pipeline: GenerationPipeline
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return Result.failure()
        val job = generationRepository.getJob(jobId) ?: return Result.failure()
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

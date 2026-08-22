package com.example.generation

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.entity.GenerationJobEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GenerationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun enqueue(job: GenerationJobEntity) {
        val request = OneTimeWorkRequestBuilder<GenerationWorker>()
            .setInputData(
                Data.Builder()
                    .putString(GenerationWorker.KEY_JOB_ID, job.id)
                    .putString(GenerationWorker.KEY_PROJECT_ID, job.projectId)
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                androidx.work.BackoffPolicy.EXPONENTIAL,
                30_000L,
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            GenerationWorker.UNIQUE_WORK_PREFIX + job.projectId,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(projectId: String) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(GenerationWorker.UNIQUE_WORK_PREFIX + projectId)
    }
}

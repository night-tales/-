package com.example.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.remote.RemoteProjectDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ProjectSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase,
    private val remote: RemoteProjectDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = database.syncOperationDao()
        val operation = dao.next() ?: return Result.success()

        return try {
            when (operation.operation) {
                "UPSERT_PROJECT" -> {
                    val project = database.projectDao().getProjectById(operation.projectId)
                    if (project != null) {
                        remote.upsertProject(project)
                    } else {
                        // The local record may have been removed after an older
                        // upsert was queued. The newer delete operation is allowed
                        // to handle remote deletion.
                    }
                }
                "DELETE_PROJECT" -> remote.deleteProject(operation.projectId)
                else -> Unit
            }

            dao.delete(operation.id)

            // KEEP prevents duplicate workers; explicitly schedule another run
            // when more queued operations remain.
            if (dao.count() > 0) {
                val request = androidx.work.OneTimeWorkRequestBuilder<ProjectSyncWorker>()
                    .setConstraints(
                        androidx.work.Constraints.Builder()
                            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
                androidx.work.WorkManager.getInstance(applicationContext)
                    .enqueueUniqueWork(
                        "project-sync",
                        androidx.work.ExistingWorkPolicy.REPLACE,
                        request
                    )
            }

            Result.success()
        } catch (error: Exception) {
            dao.recordFailure(operation.id, error.message)
            if (runAttemptCount >= 4) Result.failure() else Result.retry()
        }
    }
}

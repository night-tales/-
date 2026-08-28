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
                    if (project != null) remote.upsertProject(project)
                }
                "DELETE_PROJECT" -> remote.deleteProject(operation.projectId)
                else -> return Result.success()
            }
            dao.delete(operation.id)
            Result.success()
        } catch (error: Exception) {
            dao.recordFailure(operation.id, error.message)
            if (runAttemptCount >= 4) Result.failure() else Result.retry()
        }
    }
}

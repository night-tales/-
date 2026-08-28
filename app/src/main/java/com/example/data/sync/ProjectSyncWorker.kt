package com.example.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.remote.FirestoreGeneratedStoryDataSource
import com.example.data.remote.FirestoreProjectChildrenDataSource
import com.example.data.remote.RemoteProjectDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ProjectSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase,
    private val remote: RemoteProjectDataSource,
    private val childrenRemote: FirestoreProjectChildrenDataSource,
    private val storiesRemote: FirestoreGeneratedStoryDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = database.syncOperationDao()
        val operation = dao.next() ?: return Result.success()

        return try {
            when (operation.entityType to operation.operation) {
                "PROJECT" to "UPSERT" -> database.projectDao()
                    .getProjectById(operation.entityId)?.let(remote::upsertProject)
                "PROJECT" to "DELETE" -> remote.deleteProject(operation.projectId)
                "SCENE" to "UPSERT" -> database.sceneDao()
                    .getSceneById(operation.entityId)?.let(childrenRemote::upsertScene)
                "SCENE" to "DELETE" -> childrenRemote.deleteScene(operation.projectId, operation.entityId)
                "CHARACTER" to "UPSERT" -> database.characterDao()
                    .getCharacterById(operation.entityId)?.let(childrenRemote::upsertCharacter)
                "CHARACTER" to "DELETE" -> childrenRemote.deleteCharacter(operation.projectId, operation.entityId)
                "GENERATED_STORY" to "UPSERT" -> database.generatedStoryDao()
                    .getStoryById(operation.entityId)?.let(storiesRemote::upsertStory)
                "GENERATED_STORY" to "DELETE" -> storiesRemote.deleteStory(operation.projectId, operation.entityId)
            }

            dao.delete(operation.id)

            if (dao.count() > 0) {
                enqueueNext()
            }
            Result.success()
        } catch (error: Exception) {
            dao.recordFailure(operation.id, error.message)
            if (runAttemptCount >= 4) Result.failure() else Result.retry()
        }
    }

    private fun enqueueNext() {
        val request = OneTimeWorkRequestBuilder<ProjectSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "project-sync",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}

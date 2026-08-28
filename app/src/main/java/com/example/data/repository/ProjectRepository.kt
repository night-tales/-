package com.example.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.dao.SyncOperationDao
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.local.entity.SyncOperationEntity
import com.example.data.sync.ProjectSyncWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao,
    private val syncOperationDao: SyncOperationDao,
    private val workManager: WorkManager
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: String): ProjectEntity? =
        projectDao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity) {
        val updated = project.copy(updatedAt = System.currentTimeMillis())
        projectDao.insertProject(updated)
        enqueueSync(updated.id, "UPSERT_PROJECT")
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
        enqueueSync(id, "DELETE_PROJECT")
    }

    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>> =
        sceneDao.getScenesForProject(projectId)

    suspend fun saveScenes(scenes: List<SceneEntity>) {
        if (scenes.isEmpty()) return

        sceneDao.insertScenes(scenes)
        val projectId = scenes.first().projectId
        val project = projectDao.getProjectById(projectId)
        if (project != null) {
            val updated = project.copy(updatedAt = System.currentTimeMillis())
            projectDao.insertProject(updated)
            enqueueSync(projectId, "UPSERT_PROJECT")
        }
    }

    private suspend fun enqueueSync(projectId: String, operation: String) {
        syncOperationDao.enqueue(
            SyncOperationEntity(
                projectId = projectId,
                operation = operation
            )
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ProjectSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "project-sync",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}

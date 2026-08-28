package com.example.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.auth.AuthSession
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.GeneratedStoryDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.dao.SyncOperationDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.GeneratedStoryEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import com.example.data.local.entity.SyncOperationEntity
import com.example.data.sync.ProjectSyncWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao,
    private val characterDao: CharacterDao,
    private val generatedStoryDao: GeneratedStoryDao,
    private val syncOperationDao: SyncOperationDao,
    private val workManager: WorkManager,
    private val authSession: AuthSession
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: String): ProjectEntity? =
        projectDao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity) {
        val updated = project.copy(
            ownerId = authSession.requireUserId(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertProject(updated)
        enqueueSync("PROJECT", updated.id, updated.id, "UPSERT")
    }

    suspend fun deleteProject(id: String) {
        authSession.requireUserId()
        projectDao.deleteProject(id)
        enqueueSync("PROJECT", id, id, "DELETE")
    }

    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>> =
        sceneDao.getScenesForProject(projectId)

    suspend fun saveScenes(scenes: List<SceneEntity>) {
        authSession.requireUserId()
        if (scenes.isEmpty()) return

        sceneDao.insertScenes(scenes)
        scenes.forEach { scene ->
            enqueueSync("SCENE", scene.id, scene.projectId, "UPSERT")
        }
    }

    suspend fun saveCharacter(character: CharacterEntity) {
        authSession.requireUserId()
        characterDao.insertCharacter(character)
        enqueueSync("CHARACTER", character.id, character.projectId, "UPSERT")
    }

    suspend fun deleteCharacter(character: CharacterEntity) {
        authSession.requireUserId()
        characterDao.deleteCharacter(character.id)
        enqueueSync("CHARACTER", character.id, character.projectId, "DELETE")
    }

    suspend fun saveGeneratedStory(story: GeneratedStoryEntity) {
        val ownerId = authSession.requireUserId()
        generatedStoryDao.insertStory(story.copy(ownerId = ownerId))
        enqueueSync("GENERATED_STORY", story.id, story.projectId, "UPSERT")
    }

    suspend fun deleteGeneratedStory(story: GeneratedStoryEntity) {
        authSession.requireUserId()
        generatedStoryDao.deleteStory(story.id)
        enqueueSync("GENERATED_STORY", story.id, story.projectId, "DELETE")
    }

    private suspend fun enqueueSync(
        entityType: String,
        entityId: String,
        projectId: String,
        operation: String
    ) {
        syncOperationDao.enqueue(
            SyncOperationEntity(
                entityType = entityType,
                entityId = entityId,
                projectId = projectId,
                operation = operation
            )
        )

        val request = OneTimeWorkRequestBuilder<ProjectSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "project-sync",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}

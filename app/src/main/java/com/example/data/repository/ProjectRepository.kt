package com.example.data.repository

import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.SceneDao
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.SceneEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: String): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun saveProject(project: ProjectEntity) {
        projectDao.insertProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
    }

    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>> {
        return sceneDao.getScenesForProject(projectId)
    }

    suspend fun saveScenes(scenes: List<SceneEntity>) {
        if (scenes.isNotEmpty()) {
            sceneDao.insertScenes(scenes)
            // also update project timestamp
            val projectId = scenes.first().projectId
            val project = projectDao.getProjectById(projectId)
            if (project != null) {
                projectDao.insertProject(project.copy(updatedAt = System.currentTimeMillis()))
            }
        }
    }
}

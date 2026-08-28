package com.example.data.remote

import com.example.data.local.entity.ProjectEntity

interface RemoteProjectDataSource {
    suspend fun getProject(id: String): ProjectEntity?
    suspend fun upsertProject(project: ProjectEntity)
    suspend fun deleteProject(id: String)
}

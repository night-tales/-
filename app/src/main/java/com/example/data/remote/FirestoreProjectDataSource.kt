package com.example.data.remote

import com.example.data.local.entity.ProjectEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProjectDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteProjectDataSource {

    private val projects = firestore.collection("projects")

    override suspend fun getProject(id: String): ProjectEntity? {
        val snapshot = projects.document(id).get().await()
        if (!snapshot.exists()) return null
        return ProjectEntity(
            id = snapshot.id,
            ownerId = snapshot.getString("ownerId").orEmpty(),
            title = snapshot.getString("title").orEmpty(),
            genre = snapshot.getString("genre").orEmpty(),
            durationMinutes = snapshot.getLong("durationMinutes")?.toInt() ?: 0,
            status = snapshot.getString("status").orEmpty(),
            createdAt = snapshot.getLong("createdAt") ?: 0L,
            updatedAt = snapshot.getLong("updatedAt") ?: 0L
        )
    }

    override suspend fun upsertProject(project: ProjectEntity) {
        projects.document(project.id).set(project).await()
    }

    override suspend fun deleteProject(id: String) {
        projects.document(id).delete().await()
    }
}

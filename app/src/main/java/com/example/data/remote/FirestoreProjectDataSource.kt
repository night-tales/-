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
        return snapshot.toObject(ProjectEntity::class.java)
    }

    override suspend fun upsertProject(project: ProjectEntity) {
        projects.document(project.id).set(project).await()
    }

    override suspend fun deleteProject(id: String) {
        projects.document(id).delete().await()
    }
}

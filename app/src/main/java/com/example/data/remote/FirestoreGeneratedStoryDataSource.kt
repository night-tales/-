package com.example.data.remote

import com.example.data.local.entity.GeneratedStoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreGeneratedStoryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun ref(story: GeneratedStoryEntity) =
        firestore.collection("projects").document(story.projectId)
            .collection("generatedStories").document(story.id)

    suspend fun upsertStory(story: GeneratedStoryEntity) {
        ref(story).set(story).await()
    }

    suspend fun deleteStory(projectId: String, storyId: String) {
        firestore.collection("projects").document(projectId)
            .collection("generatedStories").document(storyId).delete().await()
    }
}

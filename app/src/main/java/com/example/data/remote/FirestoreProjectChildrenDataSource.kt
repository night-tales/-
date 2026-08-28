package com.example.data.remote

import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.SceneEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreProjectChildrenDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun upsertScene(scene: SceneEntity) {
        firestore.collection("projects").document(scene.projectId)
            .collection("scenes").document(scene.id).set(scene).await()
    }

    suspend fun deleteScene(projectId: String, sceneId: String) {
        firestore.collection("projects").document(projectId)
            .collection("scenes").document(sceneId).delete().await()
    }

    suspend fun upsertCharacter(character: CharacterEntity) {
        firestore.collection("projects").document(character.projectId)
            .collection("characters").document(character.id).set(character).await()
    }

    suspend fun deleteCharacter(projectId: String, characterId: String) {
        firestore.collection("projects").document(projectId)
            .collection("characters").document(characterId).delete().await()
    }
}

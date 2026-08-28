package com.example.data.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSession @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    fun requireUserId(): String =
        currentUserId ?: throw IllegalStateException("Authentication required")
}

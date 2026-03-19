package com.example.g45_kotlin.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): UserDto? {
        return auth.currentUser?.toDto()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }

    // Helper para convertir el usuario de Firebase al DTO
    private fun FirebaseUser.toDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )
}

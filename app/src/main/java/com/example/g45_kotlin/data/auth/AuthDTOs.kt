package com.example.g45_kotlin.data.auth

data class UserDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)

package com.example.g45_kotlin.ui.auth

import com.example.g45_kotlin.data.auth.UserDto

data class LoginState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null
)

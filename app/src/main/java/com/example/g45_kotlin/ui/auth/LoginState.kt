package com.uniandes.tutorias_g45k.ui.auth

import com.uniandes.tutorias_g45k.data.auth.UserDto

data class LoginState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null
)


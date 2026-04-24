package com.example.g45_kotlin.ui.profile

import com.example.g45_kotlin.data.firestore.FirestoreUserSummaryDto

data class ProfileScreenState (
    val user: FirestoreUserSummaryDto? = null,
    val error: String = "",
    val isLoading: Boolean = true
)
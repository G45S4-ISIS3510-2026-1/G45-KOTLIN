package com.uniandes.tutorias_g45k.ui.profile

import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto

data class ProfileScreenState (
    val user: FirestoreUserSummaryDto? = null,
    val error: String = "",
    val isLoading: Boolean = true,
    val clickedMissings: Boolean = false
)
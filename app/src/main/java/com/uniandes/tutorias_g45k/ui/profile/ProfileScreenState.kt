package com.uniandes.tutorias_g45k.ui.profile

import coil.request.ImageRequest
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto

data class ProfileScreenState (
    val user: FirestoreUserSummaryDto? = null,
    val error: String = "",
    val isLoading: Boolean = true,
    val clickedMissings: Boolean = false,
    val currentUserToken: String = ""
)
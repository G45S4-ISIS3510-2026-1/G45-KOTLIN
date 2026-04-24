package com.uniandes.tutorias_g45k.ui.profile.pages.favorites

import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto

data class FavoriteListUiState (
    val isLoading:Boolean=false,
    val favorites: List<FirestoreUserSummaryDto> = emptyList(),
    val error:String="",
    val connected:Boolean=true
)
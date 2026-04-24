package com.example.g45_kotlin.ui.profile.pages.favorites

import com.example.g45_kotlin.data.firestore.FirestoreUserSummaryDto

data class FavoriteListUiState (
    val isLoading:Boolean=false,
    val favorites: List<FirestoreUserSummaryDto> = emptyList(),
    val error:String="",
    val connected:Boolean=true
)
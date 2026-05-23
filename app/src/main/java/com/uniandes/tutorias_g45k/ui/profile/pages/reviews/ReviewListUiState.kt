package com.uniandes.tutorias_g45k.ui.profile.pages.reviews

import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto

data class ReviewListUiState (
    val isLoading:Boolean=false,
    val numReviews:Int=0,
    val reviews: List<FirestoreReviewDto> = emptyList(),
    val error:String="",
    val connected:Boolean=true
)
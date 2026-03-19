package com.example.g45_kotlin.data.user

data class TutorSummaryDto(
    val id: String,
    val name: String,
    val major: String,
    val average_rating: Double,
    val profileImageUrl: String? = null
)

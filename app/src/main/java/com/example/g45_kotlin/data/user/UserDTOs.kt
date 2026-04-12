package com.example.g45_kotlin.data.user

import com.google.gson.annotations.SerializedName

data class TutorSummaryDto(
    val id: String,
    val name: String,
    val major: String,
    val rating: Double,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    @SerializedName("session_price") val sessionPrice: Int
)

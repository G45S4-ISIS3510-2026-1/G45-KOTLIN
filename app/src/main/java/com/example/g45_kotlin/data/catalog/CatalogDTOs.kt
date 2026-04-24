package com.uniandes.tutorias_g45k.data.catalog

import com.google.gson.annotations.SerializedName

// DTO para la respuesta del listado y detalle
data class TutorResponse(
    val id: String?,
    val email: String,
    val name: String,
    val major: String="Música",
    val profileImageUrl: String?,
    val uniandesId: String?="23123231",
    val tutoringSkills: List<String> = emptyList(),
    val sessionPrice: Int = 0,
    val tutorRating: Double = 0.0,
    val receivedRatings: Int = 0,
    val bio: String? = null,
)

data class ReviewResponse(
    val id: String?,
    @SerializedName("label") val label: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("details") val details: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("authorId") val authorId: String,
    @SerializedName("authorName") val authorName: String,
    @SerializedName("authorImageUrl") val authorImageUrl: String?
)



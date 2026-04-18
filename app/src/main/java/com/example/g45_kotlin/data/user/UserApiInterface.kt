package com.example.g45_kotlin.data.user

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Body

interface UserApi {
    @GET("users/tutors/search")
    suspend fun searchTutors(): Response<List<TutorSummaryDto>>

    @GET("users/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: String): Response<UserBackDto>

    @PATCH("users/{user_id}/fav-tutors")
    suspend fun updateFavTutors(
        @Path("user_id") userId: String,
        @Body favTutors: List<String>
    ): Response<UserBackDto>
}

data class UserBackDto(
    val id: String?,
    val name: String,
    val email: String,
    val major: String,
    @SerializedName("isTutoring") val isTutoring: Boolean = false,
    @SerializedName("uniandesId") val uniandesId: Int? = null,
    @SerializedName("fcmTokens") val fcmTokens: List<String> = emptyList(),
    @SerializedName("favTutors") val favTutors: List<String> = emptyList(),
    @SerializedName("fav_tutors") val favTutorsSnake: List<String> = emptyList(),
    val profileImageUrl: String? = null
)

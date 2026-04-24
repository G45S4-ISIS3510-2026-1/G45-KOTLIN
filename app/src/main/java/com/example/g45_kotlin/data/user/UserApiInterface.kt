package com.example.g45_kotlin.data.user

import com.example.g45_kotlin.data.reservation.AvailabilityDto
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @GET("users/tutors/search")
    suspend fun searchTutors(): Response<List<TutorSummaryDto>>

    @POST("users/recommendations")
    suspend fun getRecommendations(@Body searchedTutors: List<String>): Response<List<TutorSummaryDto>>

    @GET("users/profile/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: String): Response<UserBackDto>

    @PUT("users/{user_id}")
    suspend fun updateUser(@Path("user_id") userId: String, @Body user: UserBackDto): Response<UserBackDto>

    @PATCH("users/{user_id}/tutoring-skills")
    suspend fun updateTutoringSkills(
        @Path("user_id") userId: String,
        @Body skills: List<String>
    ): Response<UserBackDto>

    @PATCH("users/{user_id}/availability")
    suspend fun updateAvailability(
        @Path("user_id") userId: String,
        @Body availability: AvailabilityDto
    ): Response<UserBackDto>

    @PATCH("users/{user_id}/session-price")
    suspend fun updateSessionPrice(
        @Path("user_id") userId: String,
        @Query("new_price") newPrice: Int
    ): Response<UserBackDto>

    @PATCH("users/{user_id}/fav-tutors")
    suspend fun updateFavTutors(
        @Path("user_id") userId: String,
        @Body favTutors: List<String>
    ): Response<UserBackDto>

    @PATCH("users/{userId}/major")
    suspend fun updateMajor(
        @Path("userId") userId: String,
        @Query("major") major: String
    ): Response<UserBackDto>
}

data class MajorUpdateRequest(
    val major: String
)

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
    val profileImageUrl: String? = null,
    @SerializedName("tutoringSkills") val tutoringSkills: List<String> = emptyList(),
    @SerializedName("availability") val availability: AvailabilityDto = AvailabilityDto(),
    @SerializedName("sessionPrice") val sessionPrice: Int = 0
)

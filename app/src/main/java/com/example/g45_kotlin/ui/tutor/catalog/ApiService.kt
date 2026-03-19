package com.example.g45_kotlin.ui.tutor.catalog

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/tutors")
    suspend fun searchTutors(
        @Query("name") name: String? = null,
        @Query("major") major: String? = null
    ): List<TutorResponse>

    @GET("users/{user_id}")
    suspend fun getTutorDetail(
        @Path("user_id") userId: String
    ): TutorResponse

    @GET("reviews/by-tutor/{tutor_id}")
    suspend fun getTutorReviews(
        @Path("tutor_id") tutorId: String
    ): List<ReviewResponse>
}

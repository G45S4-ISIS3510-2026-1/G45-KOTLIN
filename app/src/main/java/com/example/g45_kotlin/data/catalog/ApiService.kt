package com.example.g45_kotlin.data.catalog

import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.data.recommendation.TutorSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/tutors/search")
    suspend fun searchTutors(
        @Query("name") name: String? = null,
        @Query("major") major: String? = null
    ): List<TutorSummaryDto>

    @GET("users/tutor/{user_id}")
    suspend fun getTutorDetail(
        @Path("user_id") userId: String
    ): TutorResponse

    @GET("reviews/by-tutor/{tutor_id}")
    suspend fun getTutorReviews(
        @Path("tutor_id") tutorId: String
    ): List<ReviewResponse>

    @GET ("skills/by-ids")
    suspend fun getTutorSkillsByIds(@Query("ids") ids: List<String>): List<SkillSummaryDto>
}

package com.uniandes.tutorias_g45k.data.catalog

import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @GET("reviews/by-author/{author_id}")
    suspend fun getReviewsByAuthor(
        @Path("author_id") authorId: String
    ): List<ReviewResponse>

    @POST("reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): ReviewResponse

    @GET ("skills/by-ids")
    suspend fun getTutorSkillsByIds(@Query("ids") ids: List<String>): List<SkillSummaryDto>

    @GET("skills/majors")
    suspend fun getMajors(): List<String>

    @GET("skills/by-major/{major}")
    suspend fun getSkillsByMajor(@Path("major") major: String): List<SkillSummaryDto>

    @PATCH("users/{user_id}/tutoring")
    suspend fun becomeTutor(
        @Path("user_id") userId: String,
        @Query("is_tutoring") isTutoring: Boolean = true
    ): Any
}

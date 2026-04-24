package com.uniandes.tutorias_g45k.data.catalog

import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto
import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto
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


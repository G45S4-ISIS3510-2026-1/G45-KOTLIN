package com.example.g45_kotlin.data.reservation

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface SessionApi {

    @POST("sessions/")
    suspend fun createSession(@Body session: SessionDto): Response<SessionDto>

    @PATCH("sessions/{sessionId}/{participant_id}/cancel")
    suspend fun cancelSession(@Path("sessionId") sessionId: String, @Path("participant_id") participantId: String): Response<SessionDto>

    @PATCH("sessions/{sessionId}/{participant_id}/confirm")
    suspend fun confirmSession(
        @Path("sessionId") sessionId: String,
        @Path("participant_id") participantId: String,
        @Query("verif_code") verifCode: String
    ): Response<SessionDto>

    @GET("users/tutor/{userId}")
    suspend fun getParticipantData(@Path("userId") userId: String): Response<ParticipantDetailDto>

    @GET ("skills/by-ids")
    suspend fun getTutorSkillsByIds(@Query("ids") ids: List<String>): Response<List<SkillSummaryDto>>

    @GET("sessions/between/{studentId}/{tutorId}")
    suspend fun getSessionsBetween(
        @Path("studentId") studentId: String,
        @Path("tutorId") tutorId: String
    ): Response<List<SessionDto>>
}

package com.example.g45_kotlin.data.reservation

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface SessionApi {

    @POST("sessions/")
    suspend fun createSession(@Body session: SessionDto): Response<SessionDto>

    @GET("sessions/{sessionId}")
    suspend fun getSession(@Path("sessionId") sessionId: String): Response<SessionDto>

    @GET("sessions/by-student/{studentId}")
    suspend fun getSessionsByStudent(
        @Path("studentId") studentId: String,
        @Query("status_filter") statusFilter: String? = null
    ): Response<List<SessionDto>>

    @GET("sessions/by-tutor/{tutorId}")
    suspend fun getSessionsByTutor(
        @Path("tutorId") tutorId: String,
        @Query("status_filter") statusFilter: String? = null
    ): Response<List<SessionDto>>

    @GET("sessions/between/{studentId}/{tutorId}")
    suspend fun getSessionsBetween(
        @Path("studentId") studentId: String,
        @Path("tutorId") tutorId: String
    ): Response<List<SessionDto>>

    @PATCH("sessions/{sessionId}/{participant_id}/cancel")
    suspend fun cancelSession(@Path("sessionId") sessionId: String, @Path("participant_id") participantId: String): Response<SessionDto>

    @PATCH("sessions/{sessionId}/{participant_id}/confirm")
    suspend fun confirmSession(
        @Path("sessionId") sessionId: String,
        @Path("participant_id") participantId: String,
        @Query("verif_code") verifCode: String
    ): Response<SessionDto>

    @DELETE("sessions/{sessionId}")
    suspend fun deleteSession(@Path("sessionId") sessionId: String): Response<Unit>

    @GET("users/tutor/{userId}")
    suspend fun getParticipantData(@Path("userId") userId: String): Response<ParticipantDetailDto>

    @GET ("skills/by-ids")
    suspend fun getTutorSkillsByIds(@Query("ids") ids: List<String>): Response<List<SkillSummaryDto>>

}

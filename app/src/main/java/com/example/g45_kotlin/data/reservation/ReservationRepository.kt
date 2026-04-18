package com.example.g45_kotlin.data.reservation

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.g45_kotlin.data.baseUrl

object ReservationRepository {
    private val apiService by lazy {Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SessionApi::class.java)}

    suspend fun getSession (sessionId:String): Response<SessionDto> {
        Log.d("ReservationRepository", "Fetching session data...")
        return apiService.getSession(sessionId)
    }

    suspend fun sessionConfirmation (sessionId:String, verifCode:String): Response<SessionDto>{
        return apiService.confirmSession(sessionId, verifCode)
    }

    suspend fun cancelSession (sessionId:String, participantId:String): Response<SessionDto>{
        return apiService.cancelSession(sessionId, participantId)
    }

    suspend fun createSession (session: SessionDto): Response<SessionDto>{
        return apiService.createSession(session)
    }

    suspend fun getUserSessions(studentId:String): Response<List<SessionDto>> {
        return apiService.getSessionsByStudent(studentId)
    }

    suspend fun getSessionsBetween(studentId: String, tutorId: String): Response<List<SessionDto>> {
        return apiService.getSessionsBetween(studentId, tutorId)
    }

    suspend fun getTutorSkills(ids: List<String>): Response<List<SkillSummaryDto>> {
        return apiService.getTutorSkillsByIds(ids)
    }

    suspend fun getParticipantData(userId: String): Response<ParticipantDetailDto> {
        return apiService.getParticipantData(userId)
    }
}

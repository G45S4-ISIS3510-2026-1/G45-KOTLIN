package com.example.g45_kotlin.data.reservation

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ReservationRepository {
    private val apiService by lazy {Retrofit.Builder()
        .baseUrl("https://dave-umbrellaless-nonsecretively.ngrok-free.dev")
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

    suspend fun cancelSession (sessionId:String): Response<SessionDto>{
        return apiService.cancelSession(sessionId)
    }

    suspend fun getParticipant (userId:String): Response<ParticipantDto>{
        return apiService.getParticipant(userId)
    }

    suspend fun createSession (session: SessionDto): Response<SessionDto>{
        return apiService.createSession(session)
    }
}



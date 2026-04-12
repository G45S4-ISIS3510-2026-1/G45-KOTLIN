package com.example.g45_kotlin.data.user

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRepository {
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dave-umbrellaless-nonsecretively.ngrok-free.dev")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    suspend fun getTutors(): Response<List<TutorSummaryDto>> {
        Log.d("UserRepository", "Fetching tutors...")
        return apiService.searchTutors()
    }
}

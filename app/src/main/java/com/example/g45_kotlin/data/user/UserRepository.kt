package com.example.g45_kotlin.data.user

import android.util.Log
import com.example.g45_kotlin.data.baseUrl
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRepository {
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    suspend fun getTutors(): Response<List<TutorSummaryDto>> {
        Log.d("UserRepository", "Fetching tutors...")
        return apiService.searchTutors()
    }

    suspend fun getUser(userId: String): Response<UserBackDto> {
        return apiService.getUserById(userId)
    }

    suspend fun updateFavoriteTutors(userId: String, favTutors: List<String>): Response<UserBackDto> {
        return apiService.updateFavTutors(userId, favTutors)
    }
}

package com.example.g45_kotlin.data.user

import android.util.Log
import com.example.g45_kotlin.data.baseUrl
import com.example.g45_kotlin.data.local.SearchHistoryManager
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

    suspend fun getRecommendations(): Response<List<TutorSummaryDto>> {
        val searchedTutors= SearchHistoryManager.getInstance().getHistory()
        when(searchedTutors.size){
            0 -> return getTutors()
            else -> return apiService.getRecommendations(searchedTutors)
        }

    }
}

package com.example.g45_kotlin.data.user

import android.util.Log
import com.example.g45_kotlin.data.baseUrl
import com.example.g45_kotlin.data.local.SearchHistoryManager
import com.example.g45_kotlin.data.recommendation.TutorSummaryDto
import com.example.g45_kotlin.data.recommendation.UserApi
import com.example.g45_kotlin.data.recommendation.UserBackDto
import com.example.g45_kotlin.data.reservation.AvailabilityDto
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

    suspend fun getUser(userId: String): Response<UserBackDto> {
        return apiService.getUserById(userId)
    }

    suspend fun updateUser(userId: String, user: UserBackDto): Response<UserBackDto> {
        return apiService.updateUser(userId, user)
    }

    suspend fun updateTutoringSkills(userId: String, skills: List<String>): Response<UserBackDto> {
        return apiService.updateTutoringSkills(userId, skills)
    }

    suspend fun updateAvailability(userId: String, availability: AvailabilityDto): Response<UserBackDto> {
        return apiService.updateAvailability(userId, availability)
    }

    suspend fun updateSessionPrice(userId: String, newPrice: Int): Response<UserBackDto> {
        return apiService.updateSessionPrice(userId, newPrice)
    }

    suspend fun updateFavoriteTutors(userId: String, favTutors: List<String>): Response<UserBackDto> {
        return apiService.updateFavTutors(userId, favTutors)
    }

    suspend fun updateMajor(userId: String, major: String): Response<UserBackDto> {
        return apiService.updateMajor(userId, major)
    }
}

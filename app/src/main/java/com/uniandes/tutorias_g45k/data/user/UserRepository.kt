package com.uniandes.tutorias_g45k.data.user

import android.util.Log
import com.uniandes.tutorias_g45k.data.baseUrl
import com.uniandes.tutorias_g45k.data.local.SearchHistoryManager
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.data.recommendation.UserApi
import com.uniandes.tutorias_g45k.data.recommendation.UserBackDto
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
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

package com.example.g45_kotlin.data.catalog

import com.example.g45_kotlin.data.recommendation.TutorSummaryDto

class TutorRepository(private val apiService: ApiService = RetrofitClient.apiService) {
    
    suspend fun searchTutors(name: String? = null, major: String? = null): Result<List<TutorSummaryDto>> {
        return try {
            val response = apiService.searchTutors(name, major)
            val tutores = response.map { res ->
                TutorSummaryDto(
                    id=res.id,
                    name = res.name,
                    major = res.major,
                    rating=res.rating,
                    profileImageUrl = res.profileImageUrl,
                    sessionPrice = res.sessionPrice
                )
            }
            Result.success(tutores)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorDetail(userId: String): Result<TutorResponse> {
        return try {
            val res = apiService.getTutorDetail(userId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorReviews(tutorId: String): Result<List<ReviewResponse>> {
        return try {
            val response = apiService.getTutorReviews(tutorId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorSkillsByIds(ids: List<String>): Result<List<String>> {
        return try{
            val response = apiService.getTutorSkillsByIds(ids)
            val skills = response.map { res ->
                res.label
            }
            Result.success(skills)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

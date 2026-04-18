package com.example.g45_kotlin.data.catalog

import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.data.user.TutorSummaryDto

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

    suspend fun getReviewsByAuthor(authorId: String): Result<List<ReviewResponse>> {
        return try {
            val response = apiService.getReviewsByAuthor(authorId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(request: CreateReviewRequest): Result<ReviewResponse> {
        return try {
            val response = apiService.createReview(request)
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

    suspend fun getMajors(): Result<List<String>> {
        return try {
            val response = apiService.getMajors()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSkillsByMajor(major: String): Result<List<SkillSummaryDto>> {
        return try {
            val response = apiService.getSkillsByMajor(major)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun becomeTutor(userId: String): Result<Unit> {
        return try {
            apiService.becomeTutor(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

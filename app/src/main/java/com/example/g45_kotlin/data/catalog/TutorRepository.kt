package com.example.g45_kotlin.data.catalog

import android.util.LruCache
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.example.g45_kotlin.data.user.TutorSummaryDto

object TutorRepository {
    private val apiService: ApiService = RetrofitClient.apiService

    // Cache para almacenar los detalles de hasta 20 tutores
    private val tutorDetailCache = LruCache<String, TutorResponse>(20)
    
    // Cache para habilidades por carrera (máximo 15 carreras)
    private val skillsByMajorCache = LruCache<String, List<SkillSummaryDto>>(15)
    
    // Cache para reseñas por tutor (máximo 20 tutores)
    private val reviewsByTutorCache = LruCache<String, List<ReviewResponse>>(20)

    // Cache para objetos de habilidades por ID (máximo 100 habilidades)
    private val skillObjectCache = LruCache<String, SkillSummaryDto>(100)
    
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
        val cachedTutor = tutorDetailCache.get(userId)
        if (cachedTutor != null) {
            return Result.success(cachedTutor)
        }

        return try {
            val res = apiService.getTutorDetail(userId)
            tutorDetailCache.put(userId, res)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorReviews(tutorId: String): Result<List<ReviewResponse>> {
        val cachedReviews = reviewsByTutorCache.get(tutorId)
        if (cachedReviews != null) {
            return Result.success(cachedReviews)
        }

        return try {
            val response = apiService.getTutorReviews(tutorId)
            reviewsByTutorCache.put(tutorId, response)
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
            reviewsByTutorCache.remove(request.tutorId)
            tutorDetailCache.remove(request.tutorId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTutorSkillsByIds(ids: List<String>): Result<List<SkillSummaryDto>> {
        if (ids.isEmpty()) return Result.success(emptyList())

        val cachedSkills = mutableMapOf<String, SkillSummaryDto>()
        val missingIds = mutableListOf<String>()

        for (id in ids) {
            val skill = skillObjectCache.get(id)
            if (skill != null) {
                cachedSkills[id] = skill
            } else {
                missingIds.add(id)
            }
        }

        if (missingIds.isEmpty()) {
            return Result.success(ids.mapNotNull { cachedSkills[it] })
        }

        return try {
            val response = apiService.getTutorSkillsByIds(missingIds)
            response.forEach { skill ->
                skill.id?.let { id ->
                    skillObjectCache.put(id, skill)
                    cachedSkills[id] = skill
                }
            }
            
            val result = ids.mapNotNull { cachedSkills[it] }
            Result.success(result)
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
        val cachedSkills = skillsByMajorCache.get(major)
        if (cachedSkills != null) {
            return Result.success(cachedSkills)
        }

        return try {
            val response = apiService.getSkillsByMajor(major)
            skillsByMajorCache.put(major, response)
            
            response.forEach { skill ->
                skill.id?.let { id ->
                    skillObjectCache.put(id, skill)
                }
            }
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun becomeTutor(userId: String): Result<Unit> {
        return try {
            apiService.becomeTutor(userId)
            tutorDetailCache.remove(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun clearCache() {
        tutorDetailCache.evictAll()
        skillsByMajorCache.evictAll()
        reviewsByTutorCache.evictAll()
        skillObjectCache.evictAll()
    }
}

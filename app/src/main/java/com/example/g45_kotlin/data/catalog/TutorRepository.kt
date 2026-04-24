package com.uniandes.tutorias_g45k.data.catalog

import com.uniandes.tutorias_g45k.data.user.TutorSummaryDto
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor

class TutorRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val tutorDao: TutorDao
) {
    
    suspend fun searchTutors(name: String? = null, major: String? = null): Result<List<TutorSummaryDto>> {
        if (!NetworkMonitor.isOnline.value) {
            val cached = tutorDao.getAllTutors().map { it.toDto() }
            return if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(Exception("Offline y no hay datos en caché"))
            }
        }

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
            // Guardar en caché si es una búsqueda general (sin filtros para simplificar por ahora)
            if (name == null && major == null) {
                tutorDao.clearTutors()
                tutorDao.insertTutors(tutores.map { it.toEntity() })
            }
            Result.success(tutores)
        } catch (e: Exception) {
            // Intentar cargar de caché si falla la red
            val cached = tutorDao.getAllTutors().map { it.toDto() }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
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


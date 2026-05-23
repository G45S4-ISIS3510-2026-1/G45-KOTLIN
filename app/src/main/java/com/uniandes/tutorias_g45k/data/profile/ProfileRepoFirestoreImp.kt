package com.uniandes.tutorias_g45k.data.profile

import com.uniandes.tutorias_g45k.data.catalog.RetrofitClient
import com.uniandes.tutorias_g45k.data.firestore.FirestorePqrDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto
import com.uniandes.tutorias_g45k.data.firestore.ProfileListsFireStoreManager
import kotlinx.coroutines.flow.Flow


object ProfileRepoFirestoreImp: ProfileRepository {
    private val firestoreManager = ProfileListsFireStoreManager()
    private val apiService = RetrofitClient.apiService

    override suspend fun getProfile(userId: String): Result<FirestoreUserSummaryDto> {
        return try{
            val profile=firestoreManager.getUserDto(userId)
            Result.success(profile!!)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getPQRS(userId: String, dayRange: Int?): Result<List<FirestorePqrDto>> {
        return try{
            val pqrs=firestoreManager.getUserPqrs(userId, dayRange)
            Result.success(pqrs)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFavTutors(userId: String): Flow<List<FirestoreUserSummaryDto>> {
        return firestoreManager.listenToFavTutors(userId)
    }

    override fun getReviews(userId: String, tutorReviews: Boolean, dayRange: Int?): Flow<List<FirestoreReviewDto>> {
        return firestoreManager.listenToReviews(userId, tutorReviews, dayRange)
    }

    override suspend fun createPqr(request: CreatePqrRequest): Result<PqrResponse> {
        return try {
            val response = apiService.createPqr(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
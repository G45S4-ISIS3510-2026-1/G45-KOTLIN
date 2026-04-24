package com.uniandes.tutorias_g45k.data.profile

import com.uniandes.tutorias_g45k.data.firestore.FirestorePqrDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto
import com.uniandes.tutorias_g45k.data.firestore.ProfileListsFireStoreManager
import kotlinx.coroutines.flow.Flow


object ProfileRepoFirestoreImp: ProfileRepository {
    private val firestoreManager = ProfileListsFireStoreManager()
    override suspend fun getProfile(userId: String): Result<FirestoreUserSummaryDto> {
        return try{
            val profile=firestoreManager.getUserDto(userId)
            Result.success(profile!!)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getPQRS(userId: String): Result<List<FirestorePqrDto>> {
        return try{
            val pqrs=firestoreManager.getUserPqrs(userId)
            Result.success(pqrs)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFavTutors(userId: String): Flow<List<FirestoreUserSummaryDto>> {
        return firestoreManager.listenToFavTutors(userId)
    }

    override fun getReviews(userId: String): Flow<List<FirestoreReviewDto>> {
        return firestoreManager.listenToReviews(userId)
    }
}
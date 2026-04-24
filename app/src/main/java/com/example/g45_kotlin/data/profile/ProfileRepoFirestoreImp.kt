package com.example.g45_kotlin.data.profile

import com.example.g45_kotlin.data.firestore.FirestorePqrDto
import com.example.g45_kotlin.data.firestore.FirestoreReviewDto
import com.example.g45_kotlin.data.firestore.ProfileListsFireStoreManager
import com.example.g45_kotlin.data.firestore.FirestoreUserSummaryDto
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
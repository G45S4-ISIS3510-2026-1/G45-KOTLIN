package com.example.g45_kotlin.data.profile

import com.example.g45_kotlin.data.firestore.FirestorePqrDto
import com.example.g45_kotlin.data.firestore.FirestoreReviewDto
import com.example.g45_kotlin.data.firestore.FirestoreUserSummaryDto
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(userId: String): Result<FirestoreUserSummaryDto>
    fun getReviews(userId: String): Flow<List<FirestoreReviewDto>>
    fun getFavTutors(userId: String): Flow<List<FirestoreUserSummaryDto>>
    suspend fun getPQRS(userId: String): Result<List<FirestorePqrDto>>
}
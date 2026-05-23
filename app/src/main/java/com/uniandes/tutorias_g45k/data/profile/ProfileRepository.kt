package com.uniandes.tutorias_g45k.data.profile

import com.uniandes.tutorias_g45k.data.firestore.FirestorePqrDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(userId: String): Result<FirestoreUserSummaryDto>
    fun getReviews(userId: String, tutorReviews: Boolean = false, dayRange: Int? = null): Flow<List<FirestoreReviewDto>>
    fun getFavTutors(userId: String): Flow<List<FirestoreUserSummaryDto>>
    suspend fun getPQRS(userId: String): Result<List<FirestorePqrDto>>
}
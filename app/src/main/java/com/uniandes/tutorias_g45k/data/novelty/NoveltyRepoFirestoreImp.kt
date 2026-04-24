package com.uniandes.tutorias_g45k.data.novelty

import com.uniandes.tutorias_g45k.data.firestore.NoveltyFirestoreManager
import kotlinx.coroutines.flow.Flow

object NoveltyRepoFirestoreImp: NoveltyRepository {
    private val firestoreManager = NoveltyFirestoreManager()

    override fun getUnreadNovelties(userId: String, dayRange:Int?): Flow<List<NoveltyDto>> {
        return firestoreManager.listenToUnreadNovelties(userId, dayRange)
    }

    override suspend fun markNoveltyAsRead(noveltyId: String): Result<Unit> {
        return try {
            firestoreManager.markNoveltyAsRead(noveltyId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
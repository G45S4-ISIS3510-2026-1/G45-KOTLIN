package com.example.g45_kotlin.data.novelty

import com.example.g45_kotlin.data.firestore.NoveltyFirestoreManage
import kotlinx.coroutines.flow.Flow

object NoveltyRepoFirestoreImp: NoveltyRepository {
    private val firestoreManager = NoveltyFirestoreManage()

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
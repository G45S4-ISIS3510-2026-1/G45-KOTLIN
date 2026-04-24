package com.uniandes.tutorias_g45k.data.novelty

import kotlinx.coroutines.flow.Flow

interface NoveltyRepository {
    fun getUnreadNovelties(userId: String, dayRange:Int?=null): Flow<List<NoveltyDto>>
    suspend fun markNoveltyAsRead(noveltyId: String):Result<Unit>
}
package com.uniandes.tutorias_g45k.data.reservation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Query("SELECT * FROM cached_sessions ORDER BY scheduledAt ASC")
    suspend fun getUpcomingSessions(): List<CachedSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<CachedSessionEntity>)

    @Query("DELETE FROM cached_sessions")
    suspend fun clearSessions()
}

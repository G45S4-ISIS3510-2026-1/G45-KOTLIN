package com.uniandes.tutorias_g45k.data.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TutorDao {
    @Query("SELECT * FROM cached_tutors")
    suspend fun getAllTutors(): List<CachedTutorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutors(tutors: List<CachedTutorEntity>)

    @Query("DELETE FROM cached_tutors")
    suspend fun clearTutors()
}

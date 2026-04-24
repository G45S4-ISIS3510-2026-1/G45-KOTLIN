package com.uniandes.tutorias_g45k.data.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthDaoInterface {
    @Query("SELECT * FROM users")
    fun getAll(): List<UserDto>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getSavedUser(email: String): UserDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserDto): Long

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteSavedUser(email: String): Int
}

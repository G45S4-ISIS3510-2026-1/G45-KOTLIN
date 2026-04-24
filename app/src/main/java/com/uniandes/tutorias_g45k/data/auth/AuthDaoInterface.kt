package com.uniandes.tutorias_g45k.data.auth

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AuthDaoInterface {
    @Query("SELECT * FROM users")
    fun getAll(): List<UserDto>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getSavedUser(email: String): UserDto?

    @Query("INSERT INTO users (uid, email, displayName, photoUrl) VALUES (:uid, :email, :displayName, :photoUrl)")
    fun insert(uid: String, email: String, displayName: String, photoUrl: String)


    @Query("DELETE FROM users WHERE email = :email")
    fun deleteSavedUser(email: String)
}
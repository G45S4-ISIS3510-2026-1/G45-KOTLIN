package com.uniandes.tutorias_g45k.data.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto

@Dao
interface AuthDaoInterface {
    @Query("SELECT * FROM users")
    fun getAll(): List<UserDto>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getSavedUser(email: String): UserDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDto): Long

    @Query("DELETE FROM users WHERE email = :email")
    fun deleteSavedUser(email: String): Int

    @Query("""
    UPDATE users 
    SET major = :major, 
        isTutoring = :isTutoring, 
        tutoringSkills = :tutoringSkills, 
        availability = :availability, 
        sessionPrice = :sessionPrice 
    WHERE uid = :uid
""")
    fun updateTutorProfile(
        uid: String,
        major: String,
        isTutoring: Boolean,
        tutoringSkills: List<String>,
        availability: AvailabilityDto,
        sessionPrice: Int
    ): Int
}

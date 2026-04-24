package com.uniandes.tutorias_g45k.data.recommendation

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RecommendedTutorDao {
    @Query("SELECT * FROM recommended_tutors")
    fun getAll(): List<TutorSummaryDto>

    @Query("DELETE FROM recommended_tutors")
    fun clearAll(): Unit

    @Query("INSERT INTO recommended_tutors (id, name, major, rating, profile_image_url, session_price) VALUES (:id, :name, :major, :rating, :profileImageUrl, :sessionPrice)")
    fun insert(id: String, name: String, major: String, rating: Double, profileImageUrl: String?, sessionPrice: Int)
}
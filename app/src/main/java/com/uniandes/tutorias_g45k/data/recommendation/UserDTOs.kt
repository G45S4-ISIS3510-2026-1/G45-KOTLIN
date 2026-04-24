package com.uniandes.tutorias_g45k.data.recommendation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recommended_tutors")
data class TutorSummaryDto(
    @PrimaryKey val id: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("major") val major: String,
    @ColumnInfo("rating") val rating: Double,
    @ColumnInfo("profile_image_url") @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    @ColumnInfo("session_price") @SerializedName("session_price") val sessionPrice: Int
)

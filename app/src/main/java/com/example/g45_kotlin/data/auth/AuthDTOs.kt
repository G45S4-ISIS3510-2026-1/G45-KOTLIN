package com.example.g45_kotlin.data.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.g45_kotlin.data.reservation.AvailabilityDto


@Entity(tableName = "users")
data class UserDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name="displayName")val displayName: String?,
    @ColumnInfo(name="photoUrl") val photoUrl: String?
)

data class UserBackDto(
    val id: String?,
    val email: String,
    val name: String,
    val major: String="Música",
    val profileImageUrl: String?,
    val uniandesId: String?="23123231",
    val fcmTokens: List<String> = emptyList(),
    val favTutors: List<String> = emptyList(),
    val isTutoring:Boolean=false,
    val tutoringSkills: List<String> = emptyList(),
    val interestedSkills: List<String> = emptyList(),
    val availability: AvailabilityDto=AvailabilityDto(),
    val paymentMethods: List<String> = emptyList(),
    val sessionPrice: Int = 0
)

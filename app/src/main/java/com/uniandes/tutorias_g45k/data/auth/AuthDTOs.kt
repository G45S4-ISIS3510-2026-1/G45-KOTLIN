package com.uniandes.tutorias_g45k.data.auth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto


@Entity(tableName = "users")
data class UserDto(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "displayName") val displayName: String?,
    @ColumnInfo(name = "photoUrl") val photoUrl: String?,
    
    //Campos extendidos
    @ColumnInfo(name = "major") val major: String = "Música",
    @ColumnInfo(name = "uniandesId") val uniandesId: String? = "23123231",
    @ColumnInfo(name = "isTutoring") val isTutoring: Boolean = false,
    @ColumnInfo(name = "tutoringSkills") val tutoringSkills: List<String> = emptyList(),
    @ColumnInfo(name = "availability") val availability: AvailabilityDto = AvailabilityDto(),
    @ColumnInfo(name = "sessionPrice") val sessionPrice: Int = 0,
    @ColumnInfo(name = "tutorRating") val tutorRating: Double = 0.0,
    @ColumnInfo(name = "receivedRatings") val receivedRatings: Int = 0,
    @ColumnInfo(name = "favTutors") val favTutors: List<String> = emptyList()
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
    val sessionPrice: Int = 0,
    val tutorRating: Double = 0.0,
    val receivedRatings: Int = 0,
)

fun UserBackDto.toEntity() = UserDto(
    uid = id ?: "",
    email = email,
    displayName = name,
    photoUrl = profileImageUrl,
    major = major,
    uniandesId = uniandesId,
    isTutoring = isTutoring,
    tutoringSkills = tutoringSkills,
    availability = availability,
    sessionPrice = sessionPrice,
    tutorRating = tutorRating,
    receivedRatings = receivedRatings,
    favTutors = favTutors
)


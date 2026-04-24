package com.uniandes.tutorias_g45k.data.catalog

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto


@Entity(tableName = "cached_tutors")
data class CachedTutorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val major: String,
    val rating: Double,
    val profileImageUrl: String?,
    val sessionPrice: Int
)

fun TutorSummaryDto.toEntity() = CachedTutorEntity(
    id = id,
    name = name,
    major = major,
    rating = rating,
    profileImageUrl = profileImageUrl,
    sessionPrice = sessionPrice
)

fun CachedTutorEntity.toDto() = TutorSummaryDto(
    id = id,
    name = name,
    major = major,
    rating = rating,
    profileImageUrl = profileImageUrl,
    sessionPrice = sessionPrice
)

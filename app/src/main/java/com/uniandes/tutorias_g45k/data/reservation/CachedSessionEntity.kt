package com.uniandes.tutorias_g45k.data.reservation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_sessions")
data class CachedSessionEntity(
    @PrimaryKey val id: String,
    val student: ParticipantSummaryDto,
    val tutor: ParticipantSummaryDto,
    val skill: SkillSummaryDto,
    val scheduledAt: String,
    val status: String,
    val verifCode: String?,
    val price: Int?
)

fun SessionDto.toEntity() = CachedSessionEntity(
    id = id ?: "",
    student = student,
    tutor = tutor,
    skill = skill,
    scheduledAt = scheduledAt,
    status = status,
    verifCode = verifCode,
    price = price
)

fun CachedSessionEntity.toDto() = SessionDto(
    id = id,
    student = student,
    tutor = tutor,
    skill = skill,
    scheduledAt = scheduledAt,
    status = status,
    verifCode = verifCode,
    price = price
)

package com.example.g45_kotlin.data.reservation

data class SessionDto(
    val id: String?,
    val student: ParticipantSummaryDto,
    val tutor: ParticipantSummaryDto,
    val skill: SkillSummaryDto,
    val scheduledAt: String,
    val status: String,
    val verifCode: String?
)

data class SkillSummaryDto(
    val id: String?,
    val label: String
)

data class ParticipantSummaryDto(
    val id: String?,
    val name: String,
    val profileImageUrl: String?
)

data class ParticipantDetailDto(
    val id: String?,
    val email: String,
    val name: String,
    val major: String="Música",
    val profileImageUrl: String?,
    val availability: AvailabilityDto = AvailabilityDto(),
    val tutoringSkills: List<String> = emptyList(),
    val sessionPrice: Int = 0,
    val tutorRating: Double = 0.0,
    val receivedRatings: Int = 0
)

data class AvailabilityDto(
    val monday: List<String> = emptyList(),
    val tuesday: List<String> = emptyList(),
    val wednesday: List<String> = emptyList(),
    val thursday: List<String> = emptyList(),
    val friday: List<String> = emptyList(),
    val saturday: List<String> = emptyList()
)

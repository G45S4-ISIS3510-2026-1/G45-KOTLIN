package com.uniandes.tutorias_g45k.data.reservation

data class SessionDto(
    val id: String?=null,
    val student: ParticipantSummaryDto= ParticipantSummaryDto(null,"",""),
    val tutor: ParticipantSummaryDto= ParticipantSummaryDto(null,"",""),
    val skill: SkillSummaryDto= SkillSummaryDto(null, ""),
    val scheduledAt: String="",
    var status: String="Pendiente",
    val verifCode: String?="",
    val price: Int?=0
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


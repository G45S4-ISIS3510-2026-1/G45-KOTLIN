package com.example.g45_kotlin.data.reservation

data class SessionDto(
    val id: String?,
    val studentId: String,
    val tutorId: String,
    val skill: SkillDto,
    val scheduledAt: String,
    val status: String,
    val verifCode: String?
)

data class SkillDto(
    val id: String?,
    val major: String,
    val label: String,
    val iconUrl: String
)

data class ParticipantDto(
    val id:String,
    val name:String,
    val profileImageUrl:String,
    val sessionPrice:Double,
    val uniandesId:Int,
    val availability: AvailabilityDto,
    val tutoringSkills:List<String>

)

data class AvailabilityDto(
    val monday: List<String> = emptyList(),
    val tuesday: List<String> = emptyList(),
    val wednesday: List<String> = emptyList(),
    val thursday: List<String> = emptyList(),
    val friday: List<String> = emptyList(),
    val saturday: List<String> = emptyList()
)

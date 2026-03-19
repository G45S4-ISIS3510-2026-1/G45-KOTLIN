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
    val monday: List<String>,
    val tuesday: List<String>,
    val wednesday: List<String>,
    val thursday: List<String>,
    val friday: List<String>,
    val saturday: List<String>
)

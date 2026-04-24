package com.uniandes.tutorias_g45k.data.auth

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uniandes.tutorias_g45k.data.reservation.AvailabilityDto
import com.uniandes.tutorias_g45k.data.reservation.ParticipantSummaryDto
import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto

/**
 * Estos conversores permiten que Room guarde objetos complejos (Listas, Objetos)
 * transformándolos en JSON. Es buena idea para la persistencia de la Agenda.
 */
class DataConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromAvailability(value: AvailabilityDto?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAvailability(value: String): AvailabilityDto {
        return gson.fromJson(value, AvailabilityDto::class.java) ?: AvailabilityDto()
    }

    @TypeConverter
    fun fromParticipantSummary(value: ParticipantSummaryDto?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toParticipantSummary(value: String): ParticipantSummaryDto {
        return gson.fromJson(value, ParticipantSummaryDto::class.java) ?: ParticipantSummaryDto(null, "", "")
    }

    @TypeConverter
    fun fromSkillSummary(value: SkillSummaryDto?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSkillSummary(value: String): SkillSummaryDto {
        return gson.fromJson(value, SkillSummaryDto::class.java) ?: SkillSummaryDto(null, "")
    }
}

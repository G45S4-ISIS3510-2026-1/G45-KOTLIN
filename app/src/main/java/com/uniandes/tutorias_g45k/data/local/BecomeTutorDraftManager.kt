package com.uniandes.tutorias_g45k.data.local

import android.content.Context
import androidx.core.content.edit
import com.uniandes.tutorias_g45k.ui.tutor.become.TimeSlot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto

data class TutorDraft(
    val selectedSkills: Set<String>,
    val selectedMajors: Set<String>,
    val sessionPrice: Int,
    val availability: Map<String, List<TimeSlot>>
)

class BecomeTutorDraftManager(context: Context) {
    private val prefs = context.getSharedPreferences("tutor_draft_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveDraft(selectedSkills: Set<String>, selectedMajors: Set<String>, sessionPrice: Int, availability: Map<String, List<TimeSlot>>) {
        val draft = TutorDraft(
            selectedSkills = selectedSkills,
            selectedMajors = selectedMajors,
            sessionPrice = sessionPrice,
            availability = availability
        )
        prefs.edit { putString("draft_json", gson.toJson(draft)) }
    }

    fun getDraft(): TutorDraft? {
        val json = prefs.getString("draft_json", null) ?: return null
        return try {
            val type = object : TypeToken<TutorDraft>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveMajors(majors: List<String>) {
        prefs.edit { putString("cached_majors", gson.toJson(majors)) }
    }

    fun getMajors(): List<String>? {
        val json = prefs.getString("cached_majors", null) ?: return null
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveSkillsForMajor(major: String, skills: List<SkillSummaryDto>) {
        prefs.edit { putString("cached_skills_$major", gson.toJson(skills)) }
    }

    fun getSkillsForMajor(major: String): List<SkillSummaryDto>? {
        val json = prefs.getString("cached_skills_$major", null) ?: return null
        return try {
            val type = object : TypeToken<List<SkillSummaryDto>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun clearDraft() {
        prefs.edit { remove("draft_json") }
    }

    companion object {
        @Volatile
        private var INSTANCE: BecomeTutorDraftManager? = null

        fun getInstance(context: Context): BecomeTutorDraftManager {
            return INSTANCE ?: synchronized(this) {
                val instance = BecomeTutorDraftManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

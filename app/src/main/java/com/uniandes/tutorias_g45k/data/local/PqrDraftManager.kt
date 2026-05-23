package com.uniandes.tutorias_g45k.data.local

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class PqrDraft(
    val type: String,
    val selectedReason: String,
    val description: String,
    val selectedSessionId: String?
)

class PqrDraftManager(context: Context) {
    private val prefs = context.getSharedPreferences("pqr_draft_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveDraft(type: String, selectedReason: String, description: String, selectedSessionId: String?) {
        val draft = PqrDraft(
            type = type,
            selectedReason = selectedReason,
            description = description,
            selectedSessionId = selectedSessionId
        )
        prefs.edit { putString("pqr_draft_json", gson.toJson(draft)) }
    }

    fun getDraft(): PqrDraft? {
        val json = prefs.getString("pqr_draft_json", null) ?: return null
        return try {
            val type = object : TypeToken<PqrDraft>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun clearDraft() {
        prefs.edit { remove("pqr_draft_json") }
    }

    companion object {
        @Volatile
        private var INSTANCE: PqrDraftManager? = null

        fun getInstance(context: Context): PqrDraftManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PqrDraftManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

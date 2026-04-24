package com.uniandes.tutorias_g45k.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "theme_preferences")

class ThemePreferenceManager(private val context: Context)  {

    companion object{
        val PREFERENCE_KEY = booleanPreferencesKey("is_dynamic_active")
    }

    suspend fun changeDynamicThemePreference(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREFERENCE_KEY] = active
        }
    }

    val isDynamicThemeActive = context.dataStore.data.map { preferences ->
        preferences[PREFERENCE_KEY] ?: false
    }
}
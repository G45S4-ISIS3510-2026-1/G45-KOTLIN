package com.uniandes.tutorias_g45k.data.local

import android.content.Context
import androidx.core.content.edit

class SearchHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    private val maxSize = 3

    fun saveQuery(query: String) {
        val history = getHistory().toMutableList()

        history.remove(query)
        history.add(0, query)

        val limitedSet = history.take(maxSize).toSet()
        prefs.edit { putStringSet("search_history", limitedSet) }
    }

    fun getHistory(): List<String> {
        val set = prefs.getStringSet("search_history", emptySet()) ?: emptySet()
        return set.toList()
    }

    fun clearHistory(){
        prefs.edit { clear() }
    }
    companion object {
        @Volatile
        private var INSTANCE: SearchHistoryManager? = null
        fun getInstance(context: Context? = null): SearchHistoryManager {
            return INSTANCE ?: synchronized(this) {
                val instance=SearchHistoryManager(context!!)
                INSTANCE = instance
                instance
            }
        }
    }
}
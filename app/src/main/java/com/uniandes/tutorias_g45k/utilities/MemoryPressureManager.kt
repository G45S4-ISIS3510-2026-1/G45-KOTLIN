package com.uniandes.tutorias_g45k.utilities

import android.content.ComponentCallbacks2
import android.util.Log
import com.uniandes.tutorias_g45k.data.catalog.TutorRepository
import com.uniandes.tutorias_g45k.data.local.ReviewLruCache
import com.uniandes.tutorias_g45k.data.reservation.ReservationDetailCacheManager

object MemoryPressureManager {

    fun onTrimMemory(level: Int) {
        Log.d("MemoryPressureManager", "onTrimMemory level=$level")

        when {
            level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                clearAllCaches("critical/background trim")
            }

            level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
                level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW ||
                level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                trimCaches()
            }
        }
    }

    fun onLowMemory() {
        clearAllCaches("system low memory")
    }

    private fun trimCaches() {
        TutorRepository.trimCaches()
        ReservationDetailCacheManager.trimTo(20)
        ReviewLruCache.trimTo(20)
        Log.d("MemoryPressureManager", "Caches trimmed due to memory pressure.")
    }

    private fun clearAllCaches(reason: String) {
        TutorRepository.clearCache()
        ReservationDetailCacheManager.clearCache()
        ReviewLruCache.clearCache()
        Log.d("MemoryPressureManager", "All caches cleared due to $reason.")
    }
}

package com.uniandes.tutorias_g45k.data.reservation

import android.util.Log
import android.util.LruCache

object ReservationDetailCacheManager {
    private val cacheSize = 50
    private val lruCache = LruCache<String, ReservationDetailCache>(cacheSize)

    fun saveCache(cachedReservation: ReservationDetailCache) {
        Log.d("ReservationDetailCacheManager", "Saving cache for reservation ID: ${cachedReservation.reservation.id}")
        lruCache.put(cachedReservation.reservation.id, cachedReservation)
        Log.d("ReservationDetailCacheManager", "Cache size: ${lruCache.size()}")
    }

    fun getCache(id: String): ReservationDetailCache? {
        Log.d("ReservationDetailCacheManager", "Getting cache for reservation ID: $id")
        Log.d("ReservationDetailCacheManager", "Cache size: ${lruCache.size()}")
        return lruCache.get(id)
    }

    fun deleteCache(id: String) {
        lruCache.remove(id)
    }

    fun trimTo(size: Int) {
        lruCache.trimToSize(size)
        Log.d("ReservationDetailCacheManager", "Cache trimmed to size: $size")
    }

    fun clearCache() {
        lruCache.evictAll()
    }
}

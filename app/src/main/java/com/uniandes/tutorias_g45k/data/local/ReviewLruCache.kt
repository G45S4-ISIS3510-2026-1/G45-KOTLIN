package com.uniandes.tutorias_g45k.data.local

import android.util.Log
import android.util.LruCache
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto

object ReviewLruCache {

    // Cache LRU que retiene las ultimas reseñas abiertas en memoria RAM.
    private val cache = object : LruCache<String, FirestoreReviewDto>(50) {
        override fun entryRemoved(
            evicted: Boolean,
            key: String?,
            oldValue: FirestoreReviewDto?,
            newValue: FirestoreReviewDto?
        ) {
            super.entryRemoved(evicted, key, oldValue, newValue)
            if (evicted) {
                Log.d("LRU_CACHE", "Evicted review from RAM due to space limit: $key")
            }
        }
    }

    fun putReview(review: FirestoreReviewDto) {
        val id = review.id
        if (id.isNotEmpty()) {
            cache.put(id, review)
            Log.d("LRU_CACHE", "CACHE PUT: Review $id stored in RAM.")
        }
    }

    fun getReview(id: String): FirestoreReviewDto? {
        val cached = cache.get(id)
        if (cached != null) {
            Log.d("LRU_CACHE", "CACHE HIT: Review $id retrieved from RAM.")
        } else {
            Log.d("LRU_CACHE", "CACHE MISS: Review $id not found in RAM.")
        }
        return cached
    }

    fun trimTo(size: Int) {
        cache.trimToSize(size)
        Log.d("LRU_CACHE", "CACHE TRIM: Review cache reduced to $size entries.")
    }

    fun clearCache() {
        cache.evictAll()
        Log.d("LRU_CACHE", "CACHE CLEAR: Review cache cleared due to memory pressure.")
    }
}

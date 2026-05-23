package com.uniandes.tutorias_g45k.data.local

import android.util.Log
import android.util.LruCache
import com.uniandes.tutorias_g45k.data.firestore.FirestoreReviewDto

object ReviewLruCache {
    
    // Caché LRU que retiene las últimas 50 reseñas leídas en memoria RAM
    // para cumplir con los requerimientos de "Caching Strategy" de forma nativa.
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
            Log.d("LRU_CACHE", "CACHE PUT: Guardada reseña $id en memoria RAM.")
        }
    }

    fun getReview(id: String): FirestoreReviewDto? {
        val cached = cache.get(id)
        if (cached != null) {
            Log.d("LRU_CACHE", "CACHE HIT: Reseña $id leída instantáneamente de RAM.")
        } else {
            Log.d("LRU_CACHE", "CACHE MISS: Reseña $id no encontrada en memoria RAM.")
        }
        return cached
    }
}

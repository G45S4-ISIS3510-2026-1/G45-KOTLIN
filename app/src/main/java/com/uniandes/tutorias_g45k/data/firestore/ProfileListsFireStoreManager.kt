package com.uniandes.tutorias_g45k.data.firestore

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.Instant

class ProfileListsFireStoreManager {
    private val db = FirebaseFirestore.getInstance()

    fun listenToReviews(userId: String, tutorReviews:Boolean=false, dayRange: Int? = null): Flow<List<FirestoreReviewDto>> = callbackFlow {
        // Buscar todas las reseñas que escribio (tutor false) o recibio (tutor true)
        var query: Query = db.collection("reviews")
        if (tutorReviews){
            query = query.whereEqualTo("tutorId", userId)
        }else{
            query = query.whereEqualTo("authorId", userId)
        }
        // Aplicar filtro de fecha si corresponde
        if (dayRange != null) {
            val startOfDay = Instant.now().minus(Duration.ofDays(dayRange.toLong()))
            val startTimeStamp = Timestamp(startOfDay.epochSecond, startOfDay.nano)
            query = query.whereGreaterThanOrEqualTo("createdAt", startTimeStamp)
        }

        // Ordenar por fecha de creación
        query = query.orderBy("createdAt", Query.Direction.DESCENDING)

        // Registrar listener para obtener flujo directamente desde firestore
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ProfileReviewsStoreBridge", "Error en listener: ${error.message}")
                close(error) // Cierra el flow si hay un error crítico
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Aquí usamos toObjects para mapear automáticamente
                val reviews = snapshot.toObjects(FirestoreReviewDto::class.java)

                // Emitir la nueva lista al Flow
                trySend(reviews)
                Log.d("ProfileReviewsStoreBridge", "Reseñas actualizadas: ${reviews.size}")
            }
        }

        // Cerrar el listener/flujo cuando se deja de usar para evitar consumo excesivo de memoria
        awaitClose {
            Log.d("NoveltyFireStoreBridge", "Cerrando SnapshotListener")
            registration.remove()
        }
    }
    suspend fun getUserDto(userId: String): FirestoreUserSummaryDto? {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            val user=snapshot.toObject(FirestoreUserSummaryDto::class.java)
            user
        }catch(e: Exception){
            Log.d("ProfileUserFireStoreBridge", "Error getting user: ${e.message}")
            throw e
        }
    }
    suspend fun getUserPqrs(userId: String, dayRange: Int? = null): List<FirestorePqrDto> {
        return try {
            val query = db.collection("pqrs")
                .whereEqualTo("authorId", userId)
            if (dayRange != null) {
                val startOfDay = Instant.now().minus(Duration.ofDays(dayRange.toLong()))
                val startTimeStamp = Timestamp(startOfDay.epochSecond, startOfDay.nano)
                query.whereGreaterThanOrEqualTo("createdAt", startTimeStamp)
            }
            query.orderBy("createdAt", Query.Direction.DESCENDING)
            val pqrs= query.get().await().toObjects(FirestorePqrDto::class.java)
            Log.d("ProfilePQRSFireStoreBridge", "PQRS: ${pqrs}")
            pqrs
        }catch (e: Exception){
            Log.d("ProfilePQRSFireStoreBridge", "Error getting pqrs: ${e.message}")
            throw e
        }
    }

    fun listenToFavTutors(userId: String): Flow<List<FirestoreUserSummaryDto>> = callbackFlow {
        // Buscar todas las reseñas que escribio (tutor false) o recibio (tutor true)
        val userSnap = db.collection("users").document(userId).get().await().data

        val favTutors = userSnap?.get("favTutors") as? List<String> ?: emptyList()

        var query = db.collection("users").whereIn("id", favTutors).whereEqualTo("isTutor", true)

        // Registrar listener para obtener flujo directamente desde firestore
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ProfileFavTutorsStoreBridge", "Error en listener: ${error.message}")
                close(error) // Cierra el flow si hay un error crítico
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Aquí usamos toObjects para mapear automáticamente
                val favTutors = snapshot.toObjects(FirestoreUserSummaryDto::class.java)
                // Emitir la nueva lista al Flow
                trySend(favTutors)
                Log.d("ProfileFavTutorsStoreBridge", "Reseñas actualizadas: ${favTutors.size}")
            }
        }

        // Cerrar el listener/flujo cuando se deja de usar para evitar consumo excesivo de memoria
        awaitClose {
            Log.d("ProfileFavTutorsStoreBridge", "Cerrando SnapshotListener")
            registration.remove()
        }
    }


}
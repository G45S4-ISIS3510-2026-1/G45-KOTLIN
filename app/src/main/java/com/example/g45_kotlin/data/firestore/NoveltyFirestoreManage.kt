package com.example.g45_kotlin.data.firestore

import android.util.Log
import com.example.g45_kotlin.data.novelty.NoveltyDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.Instant

class NoveltyFirestoreManage {
    private val db = FirebaseFirestore.getInstance()

    //Devuelve el flujo en vivo de las notificaciones pendientes del usuario en la fecha indicada
    fun listenToUnreadNovelties(userId: String, dayRange: Int? = null): Flow<List<NoveltyDto>> = callbackFlow {
        // Buscar todas las notificaciones pendientes
        var query: Query = db.collection("novelties")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)

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
                Log.e("NoveltyFireStoreBridge", "Error en listener: ${error.message}")
                close(error) // Cierra el flow si hay un error crítico
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Aquí usamos toObjects para mapear automáticamente
                val novelties = snapshot.toObjects(NoveltyDto::class.java)

                // Emitir la nueva lista al Flow
                trySend(novelties)
                Log.d("NoveltyFireStoreBridge", "Novedades actualizadas: ${novelties.size}")
            }
        }

        // Cerrar el listener/flujo cuando se deja de usar para evitar consumo excesivo de memoria
        awaitClose {
            Log.d("NoveltyFireStoreBridge", "Cerrando SnapshotListener")
            registration.remove()
        }
    }

    suspend fun markNoveltyAsRead(noveltyId: String) {
        Log.d("NoveltyFireStoreBridge", "Marking novelty as read: $noveltyId")
        try {
            db.collection("novelties").document(noveltyId).update("isRead", true).await()
            Log.d("NoveltyFireStoreBridge", "Novelty marked as read: $noveltyId")
        } catch (e: Exception) {
            Log.e("NoveltyFireStoreBridge", "Error marking novelty as read: ${e.message}")
            throw e
        }
    }
}
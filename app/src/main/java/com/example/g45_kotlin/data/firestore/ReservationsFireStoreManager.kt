package com.example.g45_kotlin.data.firestore

import android.util.Log
import com.example.g45_kotlin.data.reservation.ParticipantSummaryDto
import com.example.g45_kotlin.data.reservation.SessionDto
import com.example.g45_kotlin.data.reservation.SkillSummaryDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.firebase.firestore.Filter
import java.time.Duration
import java.time.Instant
import java.util.Date

class ReservationsFireStoreManager {
    private val db = FirebaseFirestore.getInstance()

    fun convertTimeStamp(timestamp: Timestamp): String {
        val date: Date = timestamp.toDate()
        val colombiaZone = ZoneId.of("America/Bogota")
        val zonedDateTime = date.toInstant().atZone(colombiaZone)
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }


    //Asegurarnos de manejar las mismas data_classes hasta ahora para evitar modificaciones
    fun mapSnapshotToSession(snapshot: DocumentSnapshot): SessionDto? {
        val data = snapshot.data ?: return null
        val scheduledAt = data["scheduledAt"] as? Timestamp ?: return null
        val studentMap = data["student"] as? Map<String, Any> ?: return null
        val tutorMap = data["tutor"] as? Map<String, Any> ?: return null
        val skillMap = data["skill"] as? Map<String, Any> ?: return null


        val tutor= ParticipantSummaryDto(
            id = tutorMap["id"] as? String ?: return null,
            name = tutorMap["name"] as? String ?: "Tutor",
            profileImageUrl = tutorMap["profileImageUrl"] as? String ?: ""
        )
        val student= ParticipantSummaryDto(
            id = studentMap["id"] as? String ?: return null,
            name = studentMap["name"] as? String ?: "Estudiante",
            profileImageUrl = studentMap["profileImageUrl"] as? String ?: ""
        )
        val skill= SkillSummaryDto(
            id = skillMap["id"] as? String ?: return null,
            label = skillMap["label"] as? String ?: "Habilidad"
        )
        return SessionDto(
            id = snapshot.id,
            student = student,
            tutor = tutor,
            skill = skill,
            scheduledAt = convertTimeStamp(scheduledAt),
            status = data["status"] as? String ?: "Pendiente",
            verifCode = data["verifCode"] as? String ?: "",
            price = (data["price"] as? Number)?.toInt() ?: 0
        )
    }
    suspend fun getUpcomingUserSessions(userId: String): List<SessionDto> {
        return try {
            val nowOneHour= Instant.now().minus(Duration.ofHours(1))
            val nowOneHourTimeStamp= Timestamp(nowOneHour.epochSecond, nowOneHour.nano)
            val snapshot = db.collection("sessions")
                .where(
                    Filter.or(
                        Filter.equalTo("student.id", userId),
                        Filter.equalTo("tutor.id", userId)
                    )
                )
                .whereEqualTo("status", "Pendiente")
                .whereGreaterThanOrEqualTo("scheduledAt", nowOneHourTimeStamp)
                .orderBy("scheduledAt", Query.Direction.ASCENDING)
                .get().await()
            Log.d("ReservationsFireStoreBridge", "Sessions: ${snapshot.documents}")

            snapshot.documents.mapNotNull { mapSnapshotToSession(it) }
        }catch (e: Exception){
            Log.d("ReservationsFireStoreBridge", "Error getting sessions: ${e.message}")
            throw e
        }
    }
}



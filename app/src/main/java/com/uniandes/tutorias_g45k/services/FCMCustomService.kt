package com.uniandes.tutorias_g45k.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uniandes.tutorias_g45k.MainActivity
import com.uniandes.tutorias_g45k.R
import com.uniandes.tutorias_g45k.data.firestore.FirestoreUserSummaryDto
import com.uniandes.tutorias_g45k.data.novelty.NoveltyType
import com.uniandes.tutorias_g45k.data.profile.ProfileRepoProvider
import com.uniandes.tutorias_g45k.data.profile.ProfileRepository
import com.uniandes.tutorias_g45k.data.reservation.ReservationRepository
import com.uniandes.tutorias_g45k.data.reservation.SessionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FCMCustomService : FirebaseMessagingService() {
    private val reservationRepo= ReservationRepository
    private val profileRepo: ProfileRepository= ProfileRepoProvider.getRepository()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    //Reception logic
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        //Recover data sent in notification from backend
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            val entityId = remoteMessage.data["entity_id"]
            val title = remoteMessage.data["title"] ?: "Notificación"
            val body = remoteMessage.data["body"] ?: ""
            //Define whether the notification guides towards a tutor, or a session
            val notificationType = when (type) {
                NoveltyType.PRICE_CHANGE.label -> "tutor"
                NoveltyType.NEW_REVIEW.label -> "tutor"
                else -> "session"
            }
            runBlocking(Dispatchers.IO){
                // Get tutor/user image url to display
                val imageUrl = if (entityId != null) {
                    fetchUserImageUrl(entityId, notificationType, title, body)
                } else null

                // Use coil to retrieve said image bitmap
                val bitmap = imageUrl?.let { getTutorBitmap(this@FCMCustomService, it) }

                // Display notification with Notification Builder, defining the intents for further navigation from main activity
                showNotification(
                    title,
                    body,
                    notificationType,
                    entityId,
                    bitmap
                )
            }
        }
    }

    private suspend fun fetchUserImageUrl(entityId: String, notificationType: String, title: String, body: String): String {
        val defaultImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQf1fiSQO7JfDw0uv1Ae_Ye-Bo9nhGNg27dwg&s"

        return try {
            if (notificationType == "session") {
                val sessionResult = reservationRepo.getSession(entityId)
                val session = sessionResult.getOrNull()

                Log.d("FCM_DEBUG", "Session fetch result: ${session != null}")

                if (session != null) {
                    Log.d("FCM_DEBUG", "Session fetch successful")
                    val url = when {
                        title.contains("Nueva", ignoreCase = true) -> session.tutor.profileImageUrl
                        title.contains("confirmada", ignoreCase = true) -> session.student.profileImageUrl
                        body.contains("estudiante", ignoreCase = true) -> session.student.profileImageUrl
                        body.contains("tutor", ignoreCase = true) -> session.tutor.profileImageUrl
                        else -> {
                            Log.w("FCM_DEBUG", "No se encontró coincidencia en title/body, usando default")
                            null
                        }
                    }
                    Log.d("FCM_DEBUG", "url: $url")
                    val imageUrl = if (url != "") url else null
                    Log.d("FCM_DEBUG", "imageUrl: $imageUrl")
                    imageUrl ?: defaultImage
                } else {
                    Log.e("FCM_DEBUG", "La sesión retornó null del repositorio")
                    defaultImage
                }
            } else {
                val user = profileRepo.getProfile(entityId).getOrNull()
                user?.profileImageUrl ?: defaultImage
            }
        } catch (e: Exception) {
            Log.e("FCM_DEBUG", "Error crítico en fetchUserImageUrl: ${e.message}", e)
            defaultImage
        }
    }

    private suspend fun getTutorBitmap(context: Context, imageUrl: String): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        return (result.drawable as? BitmapDrawable)?.bitmap
    }


    private fun showNotification(title: String, body: String, type: String?, entityId: String?, profilePic: Bitmap?) {
        Log.d("FCM", "Notification received")
        val channelId = "tutoring_notifications_id"
        val id=System.currentTimeMillis().toInt()

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "Notificaciones de Tutorías"
        val descriptionText = "Avisos sobre nuevas sesiones, novedades, y cambios de precios de tutor"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)

        // Define intent with the info needed by navcontroller to redirect to the right view
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("type", type)
            putExtra("entity_id", entityId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.noti_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(profilePic)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        notificationManager.notify(id, builder.build())
    }
}
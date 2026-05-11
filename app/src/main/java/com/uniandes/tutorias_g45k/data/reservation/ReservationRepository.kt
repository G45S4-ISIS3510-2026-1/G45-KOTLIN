package com.uniandes.tutorias_g45k.data.reservation

import android.util.Log
import com.uniandes.tutorias_g45k.data.baseUrl
import com.uniandes.tutorias_g45k.data.firestore.ReservationsFireStoreManager
import com.uniandes.tutorias_g45k.ui.reservation.summary.Status
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object ReservationRepository {
    private val apiService by lazy {Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SessionApi::class.java)}

    private val reservationsFireStoreManager = ReservationsFireStoreManager()

    private val cacheLifetime = 2 // minutes


    suspend fun getSession (sessionId:String): Result<SessionDto> {
        Log.d("ReservationRepository", "Fetching session data...")
        val cache= ReservationDetailCacheManager.getCache(sessionId)
        val now= LocalDateTime.now()
        if (cache!=null){
            Log.d("ReservationRepository", "Session data found in cache: $cache")
            val minDifference= Duration.between(cache.lastUpdated, now).toMinutes()
            //Retorna directamente si el cache es fresco
            if (minDifference<cacheLifetime){
                Log.d("ReservationRepository", "Session data is fresh")
                return Result.success(cache.reservation)
            }else{
                //Si es viejo solo lo retorna si no hay forma de actualizar
                Log.d("ReservationRepository", "Session data is stale")
                cache.isStale=true
                ReservationDetailCacheManager.saveCache(cache)
                if (!NetworkMonitor.isOnline.value){
                    return Result.success(cache.reservation)
                }
            }
        }
        return try {
            val session=reservationsFireStoreManager.getSessionById(sessionId)
            if (session!=null){
                Log.d("ReservationRepository", "Session data: $session")
                val newCache=ReservationDetailCache(session, isStale = false)
                ReservationDetailCacheManager.saveCache(newCache)
                Result.success(session)
            }else{
                Log.d("ReservationRepository", "Session not found")
                ReservationDetailCacheManager.deleteCache(sessionId)
                Result.failure(Exception("La sesion indicada no fue encontrada."))
            }
        }catch (e:Exception){
            if (cache!=null){
                Result.success(cache.reservation)
            }else{
            Result.failure(Exception("La sesion indicada no fue encontrada."))
            }
        }
    }

    suspend fun sessionConfirmation (sessionId:String, participantId: String, verifCode:String): Result<SessionDto>{
        val response = apiService.confirmSession(sessionId, participantId,verifCode)
        if (response.isSuccessful){
            val cache= ReservationDetailCacheManager.getCache(sessionId)
            if (cache!=null){
                cache.reservation.status=Status.CONCLUDED.status
                ReservationDetailCacheManager.saveCache(cache)
            }
            return Result.success(response.body()!!)
        }
        return Result.failure(Exception("La sesion indicada no fue encontrada."))
    }

    suspend fun cancelSession (sessionId:String, participantId:String): Result<SessionDto>{
        val response = apiService.cancelSession(sessionId, participantId)
        if (response.isSuccessful){
            val cache= ReservationDetailCacheManager.getCache(sessionId)
            if (cache!=null){
                cache.reservation.status=Status.CANCELLED.status
                ReservationDetailCacheManager.saveCache(cache)
            }
            return Result.success(response.body()!!)
        }
        return Result.failure(Exception("La sesion indicada no fue encontrada."))
    }

    suspend fun createSession (session: SessionDto): Response<SessionDto>{
        return apiService.createSession(session)
    }

    fun getUserSessions(studentId:String, tutor:Boolean=false, dayRange:Int?=null): Flow<List<SessionDto>> {
        return reservationsFireStoreManager.listenToUserSessions(studentId, tutor, dayRange)
    }

    suspend fun getSessionsBetween(studentId: String, tutorId: String): Response<List<SessionDto>> {
        return apiService.getSessionsBetween(tutorId, studentId)
    }

    suspend fun getTutorSkills(ids: List<String>): Response<List<SkillSummaryDto>> {
        return apiService.getTutorSkillsByIds(ids)
    }

    suspend fun getParticipantData(userId: String): Response<ParticipantDetailDto> {
        return apiService.getParticipantData(userId)
    }

    suspend fun getUpcomingUserSessions(userId: String): Result<List<SessionDto>> {
        return try{
            val sessions=reservationsFireStoreManager.getUpcomingUserSessions(userId)
            Log.d("ReservationRepository", "Sessions: $sessions")
            Result.success(sessions)
        }catch (e:Exception){
            Log.d("ReservationRepository", "Error getting sessions: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserSessionsByDate(userId:String, date: LocalDate): Result<List<SessionDto>> {
        return try{
            val sessions=reservationsFireStoreManager.getUserSessionsByDate(userId, date)
            Log.d("ReservationRepository", "Sessions: $sessions")
            Result.success(sessions)
        }catch (e:Exception){
            Log.d("ReservationRepository", "Error getting sessions: ${e.message}")
            Result.failure(e)
        }
    }
}



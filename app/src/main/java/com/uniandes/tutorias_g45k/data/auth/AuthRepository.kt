package com.uniandes.tutorias_g45k.data.auth

import android.content.Context
import android.util.Log
import com.uniandes.tutorias_g45k.data.baseUrl
import com.uniandes.tutorias_g45k.data.local.SearchHistoryManager
import com.uniandes.tutorias_g45k.data.recommendation.RecommendedUserRepository
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.uniandes.tutorias_g45k.data.auth.AuthDataBase
import com.uniandes.tutorias_g45k.data.auth.UserBackDto
import com.uniandes.tutorias_g45k.data.catalog.TutorRepository
import com.uniandes.tutorias_g45k.data.reservation.ReservationDetailCacheManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository (context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val fcm= FirebaseMessaging.getInstance()
    private val db= AuthDataBase.getInstance(context)
    private val apiService by lazy {Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthUserApiInterface::class.java)}

    private val searchHistoryManager= SearchHistoryManager.getInstance()
    private val recommendedUserRepository= RecommendedUserRepository
    private val reservationCache= ReservationDetailCacheManager

    private val tutorRepository= TutorRepository

    fun getCurrentUser(): UserDto? {
        return auth.currentUser?.toDto()
    }

    suspend fun deleteAccount(){
        deleteLocalUser()
        recommendedUserRepository.clearCache()
        searchHistoryManager.clearHistory()
        reservationCache.clearCache()
        tutorRepository.clearCache()

        fcm.deleteToken()
        auth.currentUser?.delete()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    fun isUserLoggedIn(): Boolean {
        Log.d("AuthRepository", "isUserLoggedIn: ${auth.currentUser != null}")
        return auth.currentUser != null
    }


    suspend fun signOut() {
        deleteLocalUser()
        recommendedUserRepository.clearCache()
        searchHistoryManager.clearHistory()
        reservationCache.clearCache()
        tutorRepository.clearCache()

        fcm.deleteToken()
        auth.signOut()
    }

    suspend fun saveBackendUser():Result<Boolean>{
        val currentToken=await(fcm.token)
        val newUser= UserBackDto(
            id = auth.currentUser?.uid,
            email = auth.currentUser?.email ?: "",
            name = auth.currentUser?.displayName ?: "",
            profileImageUrl = auth.currentUser?.photoUrl?.toString(),
            fcmTokens = listOf(currentToken)
        )
        val response=apiService.registerUser(newUser)
        if (response.isSuccessful){
            db.userDao().insert(newUser.toEntity())
            return Result.success(true)
        }else{
            val user=FirebaseAuth.getInstance().currentUser
            user?.delete()
            return Result.failure(Exception(response.errorBody()?.string()))
        }
    }



    suspend fun saveFcmToken(){
        val currentToken=await(fcm.token)
        apiService.logDeviceToken(auth.currentUser?.uid ?: "", currentToken)
    }

    suspend fun saveLocalUser() : Result<Boolean> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
        try {
            val response = apiService.getUserProfile(uid)
            if (response.isSuccessful) {
                response.body()?.let { userBackDto ->
                    db.userDao().insert(userBackDto.toEntity())
                }
                return Result.success(true)
            } else {
                // Fallback
                return Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getLocalUser(): UserDto? {
        return db.userDao().getSavedUser(auth.currentUser?.email ?: "")
    }


    suspend fun deleteLocalUser(){
        db.userDao().deleteSavedUser(auth.currentUser?.email ?: "")
    }

    suspend fun updateLocalUser(user: UserDto){
        // Actualizar el usuario en la base de datos
        Log.d("AuthRepository", "Updating local user: $user")
        db.userDao().updateTutorProfile(
            user.uid,
            user.major,
            user.isTutoring,
            tutoringSkills = user.tutoringSkills,
            availability = user.availability,
            sessionPrice = user.sessionPrice,
        )
        Log.d("AuthRepository", "Local user updated: $user")
    }

    // Helper para convertir el usuario de Firebase al DTO
    private fun FirebaseUser.toDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

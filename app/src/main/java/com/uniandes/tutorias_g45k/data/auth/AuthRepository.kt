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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository (context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val fcm= FirebaseMessaging.getInstance()
    private val db=AuthDataBase.getInstance(context)
    private val apiService by lazy {Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthUserApiInterface::class.java)}

    private val searchHistoryManager= SearchHistoryManager.getInstance()
    private val recommendedUserRepository= RecommendedUserRepository

    fun getCurrentUser(): UserDto? {
        return auth.currentUser?.toDto()
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
        fcm.deleteToken()
        auth.signOut()
    }

    suspend fun saveBackendUser(){
        val currentToken=await(fcm.token)
        val newUser=UserBackDto(
            id=auth.currentUser?.uid,
            email = auth.currentUser?.email ?: "",
            name = auth.currentUser?.displayName ?: "",
            profileImageUrl = auth.currentUser?.photoUrl?.toString(),
            fcmTokens = listOf(currentToken)
        )
        apiService.registerUser(newUser)
    }



    suspend fun saveFcmToken(){
        val currentToken=await(fcm.token)
        apiService.logDeviceToken(auth.currentUser?.uid ?: "", currentToken)
    }

    suspend fun saveLocalUser() {
        val currentUser = auth.currentUser?.toDto() ?: return
        db.userDao().insert(currentUser.uid, currentUser.email ?: "", currentUser.displayName ?: "", currentUser.photoUrl?.toString() ?: "")
    }

    suspend fun getLocalUser(): UserDto? {
        return db.userDao().getSavedUser(auth.currentUser?.email ?: "")
    }

    suspend fun deleteLocalUser(){
        db.userDao().deleteSavedUser(auth.currentUser?.email ?: "")
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

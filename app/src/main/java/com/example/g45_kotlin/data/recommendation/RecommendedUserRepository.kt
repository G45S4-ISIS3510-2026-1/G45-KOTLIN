package com.example.g45_kotlin.data.recommendation

import android.util.Log
import com.example.g45_kotlin.data.baseUrl
import com.example.g45_kotlin.data.local.SearchHistoryManager
import com.example.g45_kotlin.utilities.NetworkMonitor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecommendedUserRepository {
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecommendationApi::class.java)
    }


    private val db=RecommendationDatabase.getInstance()

    fun clearCache(){
        db.recommendationDao().clearAll()
    }

    suspend fun getRecommendations(): Result<List<TutorSummaryDto>> {
        val searchedTutors= SearchHistoryManager.getInstance().getHistory()
        val isConnected=NetworkMonitor.isOnline.value
        if(!isConnected){
            //Sin red dependemos exclusivamente de la base de datos local
            Log.d("RecommendedUserRepository", "No hay conexión a internet")
            val recommendations=db.recommendationDao().getAll()
            if (recommendations.isEmpty()){
                return Result.failure(Exception("Error al cargar tutores recomendados"))
            }
            return Result.success(recommendations)
        }else{
            //Network first
            val response = apiService.getRecommendations(searchedTutors)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("RecommendedUserRepository", "Tutores recomendados cargados exitosamente")
                    db.recommendationDao().clearAll()
                    Log.d("RecommendedUserRepository", "Tutores recomendados previos borrados exitosamente")
                    responseBody.forEach {
                        db.recommendationDao().insert(it.id, it.name, it.major, it.rating, it.profileImageUrl, it.sessionPrice)
                    }
                    Log.d("RecommendedUserRepository", "Tutores recomendados guardados exitosamente")
                    return Result.success(responseBody)
                }else{
                    return Result.failure(Exception("Error al cargar tutores recomendados"))
                }
            }else{
                //Falls back to cache
                val recommendations=db.recommendationDao().getAll()
                if (recommendations.isEmpty()){
                    return Result.failure(Exception("Error al cargar tutores recomendados"))
                }
                return Result.success(recommendations)
            }
        }
    }
}

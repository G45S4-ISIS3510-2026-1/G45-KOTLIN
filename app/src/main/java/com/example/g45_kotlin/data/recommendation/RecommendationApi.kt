package com.example.g45_kotlin.data.recommendation

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationApi {
    @POST("recommendations")
    suspend fun getRecommendations(@Body searchedTutors:List<String>): Response<List<TutorSummaryDto>>
}

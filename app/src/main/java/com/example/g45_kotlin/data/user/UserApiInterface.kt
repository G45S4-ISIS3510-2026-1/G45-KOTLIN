package com.uniandes.tutorias_g45k.data.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("users/tutors/search")
    suspend fun searchTutors(): Response<List<TutorSummaryDto>>

    @POST("recommendations")
    suspend fun getRecommendations(@Body searchedTutors:List<String>): Response<List<TutorSummaryDto>>
}


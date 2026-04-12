package com.example.g45_kotlin.data.user

import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("users/tutors/search")
    suspend fun searchTutors(): Response<List<TutorSummaryDto>>
}

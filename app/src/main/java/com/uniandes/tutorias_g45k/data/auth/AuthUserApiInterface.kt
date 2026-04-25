package com.uniandes.tutorias_g45k.data.auth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthUserApiInterface {
    @POST("users")
    suspend fun registerUser(@Body user: UserBackDto): Response<UserBackDto>

    @POST("users/{user_id}/login")
    suspend fun logDeviceToken(@Path("user_id") userId: String,
                               @Query("fcm_token") fcmToken: String): Response<UserBackDto>

    @GET("users/profile/{user_id}")
    suspend fun getUserProfile(@Path("user_id") userId: String): Response<UserBackDto>

}


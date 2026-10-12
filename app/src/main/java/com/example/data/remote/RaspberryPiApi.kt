package com.example.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RaspberryPiApi {
    @GET("api/live")
    suspend fun getLiveTelemetry(): Response<RaspberryPiPayload>

    @GET("api/health")
    suspend fun getSystemHealth(): Response<SystemHealthDto>

    @POST("api/control/cooling")
    suspend fun toggleCooling(@Query("enabled") enabled: Boolean): Response<Map<String, Boolean>>

    @POST("api/control/fans")
    suspend fun toggleFans(@Query("enabled") enabled: Boolean): Response<Map<String, Boolean>>
}

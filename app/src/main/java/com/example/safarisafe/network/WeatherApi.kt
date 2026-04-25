package com.example.safarisafe.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Data classes matching the Google Weather API response
data class GoogleWeatherResponse(
    val temperature: TemperatureData,
    val weatherCondition: WeatherConditionData
)
data class TemperatureData(val degrees: Double)
data class WeatherConditionData(val description: DescriptionData)
data class DescriptionData(val text: String)

// 2. The API Interface
interface WeatherApiService {
    @GET("v1/currentConditions:lookup")
    suspend fun getCurrentConditions(
        @Query("key") apiKey: String,
        @Query("location.latitude") lat: Double,
        @Query("location.longitude") lng: Double,
        @Query("unitsSystem") units: String = "METRIC"
    ): GoogleWeatherResponse
}

// 3. The Singleton Object to use anywhere in your app
object WeatherNetwork {
    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://weather.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
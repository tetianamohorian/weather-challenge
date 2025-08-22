package weather

import retrofit2.http.GET
import retrofit2.http.Query


// Retrofit interface for accessing the WeatherAPI forecast endpoint
interface WeatherApi {
    // Fetches the weather forecast (current + N days ahead)
    @GET("forecast.json")
    suspend fun forecast(
        @Query("key") key: String, // Your WeatherAPI API key
        @Query("q") query: String, // Your WeatherAPI API key
        @Query("days") days: Int = 2, // Number of days to forecast (2 = today + tomorrow)
        @Query("aqi") aqi: String = "no",  // Whether to include air quality info (disabled)
        @Query("alerts") alerts: String = "no" // Whether to include weather alerts (disabled)
    ): ForecastResponse
}


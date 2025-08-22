package weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun forecast(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("days") days: Int = 2, // today + tomorrow
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): ForecastResponse
}

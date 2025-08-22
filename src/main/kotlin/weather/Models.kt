package weather

// Top-level response object returned by the API
data class ForecastResponse(
    val location: Location? = null,
    val forecast: Forecast? = null
)

// Contains basic location metadata (city name, country, timezone)
data class Location(
    val name: String? = null,
    val country: String? = null,
    val tz_id: String? = null
)

// Contains a list of forecast days
data class Forecast(
    val forecastday: List<ForecastDay> = emptyList()
)

// Represents a single day in the forecast, including hourly data
data class ForecastDay(
    val date: String, // Date in format "YYYY-MM-DD"
    val day: Day, // Aggregated day statistics
    val hour: List<Hour> = emptyList()// Hour-by-hour data for the day
)

data class Day(
    val mintemp_c: Double, // Minimum temperature (°C)
    val maxtemp_c: Double, // Maximum temperature (°C)
    val avghumidity: Double, // Average humidity (%)
    val maxwind_kph: Double // Maximum wind speed (kph)
)

// Hour-by-hour weather data
data class Hour(
    val time: String,  // Time in format "YYYY-MM-DD HH:mm"
    val wind_kph: Double? = null, // Wind speed at this hour
    val wind_degree: Int? = null,  // Wind direction in degrees
    val wind_dir: String? = null // Wind direction as compass value (e.g. "NW")
)


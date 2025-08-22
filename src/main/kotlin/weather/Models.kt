package weather

data class ForecastResponse(
    val location: Location? = null,
    val forecast: Forecast? = null
)

data class Location(
    val name: String? = null,
    val country: String? = null,
    val tz_id: String? = null
)

data class Forecast(
    val forecastday: List<ForecastDay> = emptyList()
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour> = emptyList()
)

data class Day(
    val mintemp_c: Double,
    val maxtemp_c: Double,
    val avghumidity: Double,
    val maxwind_kph: Double
)

data class Hour(
    val time: String,
    val wind_kph: Double? = null,
    val wind_degree: Int? = null,
    val wind_dir: String? = null
)

package weather

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.roundToInt
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.awaitAll

// Represents a single cell in the output table
data class Cell(val text: String)


// Represents a single row in the output table
data class Row(val city: String, val byDate: Map<String, Cell>)



// Creates and configures Retrofit + Moshi API client
private fun apiClient(): WeatherApi {
    val logging = HttpLoggingInterceptor().apply {
        // Disable logging for cleaner output
        level = HttpLoggingInterceptor.Level.NONE
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(WeatherApi::class.java)
}

fun main() = runBlocking {
     // Get API key from environment variable
    val apiKey = System.getenv("WEATHER_API_KEY")
        ?: error("Please set WEATHER_API_KEY environment variable")

    // Optionally get cities from environment variable
    val citiesArg = System.getenv("CITIES")
    val cities = (citiesArg?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
        ?: listOf("Chisinau", "Madrid", "Kyiv", "Amsterdam"))

    val api = apiClient()

    // Fetch weather data for all cities in parallel
    val results = cities.map { city ->
        async {
            val resp = api.forecast(apiKey, city)
            city to resp
        }
    }.awaitAll()

    // Extract tomorrow's forecast and build table rows
    val rows = results.map { (city, resp) ->
        val fdays = resp.forecast?.forecastday ?: emptyList()
        val next = fdays.getOrNull(1)
        val cellText = if (next != null) {
            val d = next.day
            val min = d.mintemp_c
            val max = d.maxtemp_c
            val hum = d.avghumidity.roundToInt()
            val maxWind = d.maxwind_kph
            val degree = "'"

            // Determine dominant wind direction (most frequent value)
            val dir = next.hour
                .mapNotNull { it.wind_dir }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "N/A"

            // Format output string for the cell
            "min=${"%.1f".format(min)}'C, " +
                    "max=${"%.1f".format(max)}'C, " +
                    "hum=${hum}%, " +
                    "wind=${"%.1f".format(maxWind)} kph, " +
                    "dir=${dir}"
        } else {
            "N/A"
        }
        val date = next?.date ?: "N/A"
        Row(city, mapOf(date to Cell(cellText)))
    }

     // Extract all unique dates used as table columns (usually one: tomorrow)
    val allDates = rows.flatMap { it.byDate.keys }.toSet().toList().sorted()
    // Print formatted table
    printTable(rows, allDates)
}

// Pretty-prints the table to STDOUT
private fun printTable(rows: List<Row>, dates: List<String>) {
    val cityHeader = "City"
    val cityWidth = (listOf(cityHeader) + rows.map { it.city }).maxOf { it.length }.coerceAtLeast(6)

    val dateWidths = dates.associateWith { date ->
        val cells = rows.map { it.byDate[date]?.text ?: "" }
        (listOf(date) + cells).maxOf { it.length }.coerceAtLeast(24)
    }

    fun pad(s: String, w: Int) = if (s.length >= w) s else s + " ".repeat(w - s.length)

    // Print table header
    val header = buildString {
        append(pad(cityHeader, cityWidth))
        dates.forEach { d -> append(" | ").append(pad(d, dateWidths[d]!!)) }
    }
    println(header)
    println("-".repeat(header.length))

    // Print each row
    rows.forEach { r ->
        val line = buildString {
            append(pad(r.city, cityWidth))
            dates.forEach { d ->
                val cell = r.byDate[d]?.text ?: ""
                append(" | ").append(pad(cell, dateWidths[d]!!))
            }
        }
        println(line)
    }
}

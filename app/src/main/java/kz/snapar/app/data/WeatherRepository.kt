package kz.snapar.app.data

import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.DailyForecast
import kz.snapar.app.model.LiveWeather
import kz.snapar.app.model.LocalText
import kz.snapar.app.model.Place
import kz.snapar.app.model.Weather
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class WeatherRepository {
    fun fetch(place: Place): Result<LiveWeather> = runCatching {
        val endpoint = buildString {
            append("https://api.open-meteo.com/v1/forecast")
            append("?latitude=${place.latitude}&longitude=${place.longitude}")
            append("&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,surface_pressure")
            append("&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset")
            append("&timezone=auto&forecast_days=7")
        }
        val connection = URL(endpoint).openConnection() as HttpURLConnection
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        try {
            if (connection.responseCode !in 200..299) {
                error("Weather service returned ${connection.responseCode}")
            }
            val root = connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
            val current = root.getJSONObject("current")
            val dailyJson = root.getJSONObject("daily")
            val dates = dailyJson.getJSONArray("time")
            val codes = dailyJson.getJSONArray("weather_code")
            val max = dailyJson.getJSONArray("temperature_2m_max")
            val min = dailyJson.getJSONArray("temperature_2m_min")
            val rain = dailyJson.getJSONArray("precipitation_probability_max")
            val sunrise = dailyJson.getJSONArray("sunrise")
            val sunset = dailyJson.getJSONArray("sunset")
            val daily = (0 until dates.length()).map { index ->
                DailyForecast(
                    date = dates.getString(index),
                    weatherCode = codes.getInt(index),
                    temperatureMax = max.getDouble(index).roundToInt(),
                    temperatureMin = min.getDouble(index).roundToInt(),
                    precipitationPercent = rain.optInt(index, 0),
                    sunrise = sunrise.getString(index).takeLast(5),
                    sunset = sunset.getString(index).takeLast(5),
                )
            }
            val code = current.getInt("weather_code")
            LiveWeather(
                current = Weather(
                    temperature = current.getDouble("temperature_2m").roundToInt(),
                    condition = weatherCondition(code),
                    humidity = current.getDouble("relative_humidity_2m").roundToInt(),
                    windKmh = current.getDouble("wind_speed_10m").roundToInt(),
                    pressure = current.getDouble("surface_pressure").roundToInt(),
                    sunrise = daily.firstOrNull()?.sunrise ?: place.weather.sunrise,
                    sunset = daily.firstOrNull()?.sunset ?: place.weather.sunset,
                ),
                daily = daily,
                updatedAtMillis = System.currentTimeMillis(),
            )
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        fun weatherCondition(code: Int): LocalText = when (code) {
            0 -> LocalText("Ашық", "Ясно", "Clear")
            1, 2 -> LocalText("Ала бұлтты", "Переменная облачность", "Partly cloudy")
            3 -> LocalText("Бұлтты", "Облачно", "Overcast")
            45, 48 -> LocalText("Тұман", "Туман", "Fog")
            51, 53, 55, 56, 57 -> LocalText("Сіркіреме", "Морось", "Drizzle")
            61, 63, 65, 66, 67, 80, 81, 82 -> LocalText("Жаңбыр", "Дождь", "Rain")
            71, 73, 75, 77, 85, 86 -> LocalText("Қар", "Снег", "Snow")
            95, 96, 99 -> LocalText("Найзағай", "Гроза", "Thunderstorm")
            else -> LocalText("Құбылмалы", "Переменная", "Variable")
        }

        fun condition(code: Int, language: AppLanguage): String = weatherCondition(code).value(language)
    }
}

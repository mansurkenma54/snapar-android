package kz.snapar.app.model

import androidx.annotation.DrawableRes

enum class AppScreen {
    Discover,
    Routes,
    Sai,
    Passport,
    Profile,
    GeoSnap,
    Business,
}

enum class AppLanguage(val code: String, val label: String) {
    Kazakh("kk", "Қазақша"),
    Russian("ru", "Русский"),
    English("en", "English"),
}

enum class AppTheme(val code: String) {
    System("system"),
    Light("light"),
    Dark("dark"),
}

data class LocalText(
    val kk: String,
    val ru: String,
    val en: String,
) {
    fun value(language: AppLanguage): String = when (language) {
        AppLanguage.Kazakh -> kk
        AppLanguage.Russian -> ru
        AppLanguage.English -> en
    }
}

data class Weather(
    val temperature: Int,
    val condition: LocalText,
    val humidity: Int,
    val windKmh: Int,
    val pressure: Int,
    val sunrise: String,
    val sunset: String,
)

data class Place(
    val id: Int,
    val name: LocalText,
    val region: LocalText,
    val description: LocalText,
    @DrawableRes val image: Int,
    val rating: Double,
    val trustScore: Int,
    val weather: Weather,
    val distanceKm: Int,
    val travelMinutes: Int,
    val averageCost: Int,
    val bestSeason: LocalText,
    val difficulty: LocalText,
    val tags: List<LocalText>,
    val recommendation: LocalText,
    val latitude: Double,
    val longitude: Double,
    @DrawableRes val communityImages: List<Int>,
)

data class RouteStop(
    val time: String,
    val title: LocalText,
    val note: LocalText,
    val cost: Int,
)

data class RouteDay(
    val day: Int,
    val title: LocalText,
    val stops: List<RouteStop>,
)

data class TravelRoute(
    val id: Int,
    val title: LocalText,
    @DrawableRes val image: Int,
    val days: Int,
    val rating: Double,
    val trust: Int,
    val price: Int,
    val distanceKm: Int,
    val interests: Set<String>,
    val itinerary: List<RouteDay>,
    val transport: LocalText = LocalText("", "", ""),
    val lodging: LocalText = LocalText("", "", ""),
    val pace: LocalText = LocalText("", "", ""),
    val departureDate: String = "",
)

data class CommunityPost(
    val id: String,
    val user: String,
    val location: LocalText,
    val caption: LocalText,
    val image: Any,
    val likes: Int,
    val comments: List<String>,
    val is360: Boolean = false,
    val verifiedGps: Boolean = true,
    val mediaType: String = "image",
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val route: TravelRoute? = null,
)

data class CalendarOffer(
    val day: Int,
    val temperature: Int,
    val condition: LocalText,
    val hotelPrice: Int,
    val crowdPercent: Int,
)

data class DailyForecast(
    val date: String,
    val weatherCode: Int,
    val temperatureMax: Int,
    val temperatureMin: Int,
    val precipitationPercent: Int,
    val sunrise: String,
    val sunset: String,
)

data class LiveWeather(
    val current: Weather,
    val daily: List<DailyForecast>,
    val updatedAtMillis: Long,
)

data class VenueListing(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val averagePrice: Int,
    val description: String,
    val status: String = "review",
    val imageUri: String = "",
)

data class AppNotification(
    val id: String,
    val title: LocalText,
    val message: LocalText,
    val time: LocalText,
    val isRead: Boolean = false,
)

data class Review(
    val id: String,
    val user: String,
    val rating: Float,
    val text: LocalText,
    val gpsVerified: Boolean,
    val date: LocalText,
    val helpful: Int = 0,
)

enum class FairnessVerdict { Fair, Cheap, Overpriced }

data class PriceFairness(
    val marketAvg: Int,
    val verdict: FairnessVerdict,
    val score: Int,
    val tip: LocalText,
)

data class BookingSlot(
    val dateLabel: LocalText,
    val price: Int,
    val available: Boolean = true,
)

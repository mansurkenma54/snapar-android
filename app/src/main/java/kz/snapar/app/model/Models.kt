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

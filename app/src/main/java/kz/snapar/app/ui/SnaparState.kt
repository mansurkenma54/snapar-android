package kz.snapar.app.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kz.snapar.app.data.AppPreferences
import kz.snapar.app.data.SampleData
import kz.snapar.app.data.WeatherRepository
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.AppNotification
import kz.snapar.app.model.AppTheme
import kz.snapar.app.model.ChatMessage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.LiveWeather
import kz.snapar.app.model.LocalText
import kz.snapar.app.model.Place
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.model.VenueListing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class SnaparState(
    context: Context,
    private val scope: CoroutineScope,
) {
    private val preferences = AppPreferences(context.applicationContext)
    private val weatherRepository = WeatherRepository()

    var loaded by mutableStateOf(false)
        private set
    var onboardingDone by mutableStateOf(false)
        private set
    var language by mutableStateOf(AppLanguage.Kazakh)
        private set
    var likedIds by mutableStateOf(emptySet<Int>())
        private set
    var dislikedIds by mutableStateOf(emptySet<Int>())
        private set
    var savedIds by mutableStateOf(emptySet<Int>())
        private set
    var visitedIds by mutableStateOf(emptySet<Int>())
        private set
    var notificationsEnabled by mutableStateOf(true)
        private set
    var businessMode by mutableStateOf(false)
        private set
    var theme by mutableStateOf(AppTheme.System)
        private set
    var largeText by mutableStateOf(false)
        private set
    var reducedMotion by mutableStateOf(false)
        private set
    var publishedCount by mutableIntStateOf(0)
        private set
    var routeCount by mutableIntStateOf(0)
        private set
    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set

    val sessionPosts = mutableStateListOf<CommunityPost>()
    val generatedRoutes = mutableStateListOf<TravelRoute>()
    val saiMessages = mutableStateListOf<ChatMessage>()
    val venues = mutableStateListOf<VenueListing>()
    var likedPostIds by mutableStateOf(emptySet<String>())
        private set
    var savedRouteIds by mutableStateOf(emptySet<Int>())
        private set
    var readNotificationIds by mutableStateOf(emptySet<String>())
        private set
    val postComments = mutableStateMapOf<String, List<String>>()
    val liveWeather = mutableStateMapOf<Int, LiveWeather>()
    var weatherLoadingIds by mutableStateOf(emptySet<Int>())
        private set
    val weatherErrors = mutableStateMapOf<Int, String>()

    val xp: Int get() = visitedIds.size * 250 + publishedCount * 75 + routeCount * 50
    val level: Int get() = (xp / 500) + 1
    val traveledKm: Int get() = SampleData.places.filter { it.id in visitedIds }.sumOf { it.distanceKm }
    val notifications: List<AppNotification>
        get() = notificationCatalog.map { it.copy(isRead = it.id in readNotificationIds) }
    val unreadNotifications: Int get() = notifications.count { !it.isRead }

    init {
        scope.launch {
            preferences.values.collect { stored ->
                onboardingDone = stored.onboardingDone
                language = AppLanguage.entries.firstOrNull { it.code == stored.language } ?: AppLanguage.Kazakh
                likedIds = stored.likedIds.mapNotNull(String::toIntOrNull).toSet()
                dislikedIds = stored.dislikedIds.mapNotNull(String::toIntOrNull).toSet()
                savedIds = stored.savedIds.mapNotNull(String::toIntOrNull).toSet()
                visitedIds = stored.visitedIds.mapNotNull(String::toIntOrNull).toSet()
                notificationsEnabled = stored.notificationsEnabled
                businessMode = stored.businessMode
                theme = AppTheme.entries.firstOrNull { it.code == stored.theme } ?: AppTheme.System
                largeText = stored.largeText
                reducedMotion = stored.reducedMotion
                publishedCount = stored.publishedCount
                routeCount = stored.routeCount
                userName = stored.userName
                userEmail = stored.userEmail
                likedPostIds = stored.likedPostIds
                savedRouteIds = stored.savedRouteIds.mapNotNull(String::toIntOrNull).toSet()
                readNotificationIds = stored.readNotificationIds
                if (!loaded) {
                    stored.postComments.mapNotNull(::decodeComment).forEach { (postId, comment) ->
                        postComments[postId] = postComments[postId].orEmpty() + comment
                    }
                    sessionPosts += stored.publishedPosts.mapNotNull(::decodePost).sortedByDescending { it.id }
                    generatedRoutes += stored.generatedRoutes.mapNotNull(::decodeRoute)
                    venues += stored.venues.mapNotNull(::decodeVenue)
                }
                loaded = true
            }
        }
    }

    fun finishOnboarding() {
        onboardingDone = true
        scope.launch { preferences.setOnboardingDone(true) }
    }

    fun completeRegistration(name: String, email: String) {
        userName = name.trim()
        userEmail = email.trim()
        scope.launch {
            preferences.setUserName(userName)
            preferences.setUserEmail(userEmail)
        }
        finishOnboarding()
    }

    fun updateLanguage(value: AppLanguage) {
        language = value
        scope.launch { preferences.setLanguage(value.code) }
    }

    fun toggleLike(id: Int) {
        likedIds = if (id in likedIds) likedIds - id else likedIds + id
        dislikedIds = dislikedIds - id
        scope.launch {
            preferences.setLiked(likedIds.map(Int::toString).toSet())
            preferences.setDisliked(dislikedIds.map(Int::toString).toSet())
        }
    }

    fun dislike(id: Int) {
        dislikedIds = dislikedIds + id
        likedIds = likedIds - id
        scope.launch {
            preferences.setLiked(likedIds.map(Int::toString).toSet())
            preferences.setDisliked(dislikedIds.map(Int::toString).toSet())
        }
    }

    fun toggleSaved(id: Int) {
        savedIds = if (id in savedIds) savedIds - id else savedIds + id
        scope.launch { preferences.setSaved(savedIds.map(Int::toString).toSet()) }
    }

    fun checkIn(id: Int): Boolean {
        if (id in visitedIds) return false
        visitedIds = visitedIds + id
        scope.launch { preferences.setVisited(visitedIds.map(Int::toString).toSet()) }
        return true
    }

    fun setNotifications(value: Boolean) {
        notificationsEnabled = value
        scope.launch { preferences.setNotifications(value) }
    }

    fun updateBusinessMode(value: Boolean) {
        businessMode = value
        scope.launch { preferences.setBusinessMode(value) }
    }

    fun updateTheme(value: AppTheme) {
        theme = value
        scope.launch { preferences.setTheme(value.code) }
    }

    fun updateLargeText(value: Boolean) {
        largeText = value
        scope.launch { preferences.setLargeText(value) }
    }

    fun updateReducedMotion(value: Boolean) {
        reducedMotion = value
        scope.launch { preferences.setReducedMotion(value) }
    }

    fun publish(post: CommunityPost) {
        sessionPosts.add(0, post)
        publishedCount += 1
        scope.launch {
            preferences.setPublishedCount(publishedCount)
            preferences.setPublishedPosts(sessionPosts.map(::encodePost).toSet())
        }
    }

    fun saveGeneratedRoute(route: TravelRoute) {
        if (generatedRoutes.none { it.id == route.id }) {
            generatedRoutes.add(0, route)
            routeCount += 1
        }
        scope.launch {
            preferences.setRouteCount(routeCount)
            preferences.setGeneratedRoutes(generatedRoutes.map(::encodeRoute).toSet())
        }
    }

    fun toggleRouteSaved(route: TravelRoute) {
        savedRouteIds = if (route.id in savedRouteIds) savedRouteIds - route.id else savedRouteIds + route.id
        scope.launch { preferences.setSavedRouteIds(savedRouteIds.map(Int::toString).toSet()) }
    }

    fun togglePostLike(postId: String) {
        likedPostIds = if (postId in likedPostIds) likedPostIds - postId else likedPostIds + postId
        scope.launch { preferences.setLikedPostIds(likedPostIds) }
    }

    fun commentsFor(post: CommunityPost): List<String> = post.comments + postComments[post.id].orEmpty()

    fun addComment(postId: String, comment: String) {
        val clean = comment.trim()
        if (clean.isEmpty()) return
        postComments[postId] = postComments[postId].orEmpty() + clean
        scope.launch {
            preferences.setPostComments(
                postComments.flatMap { (id, comments) -> comments.map { encodeFields(id, it) } }.toSet(),
            )
        }
    }

    fun upsertVenue(venue: VenueListing) {
        val existing = venues.indexOfFirst { it.id == venue.id }
        if (existing >= 0) venues[existing] = venue else venues.add(0, venue)
        scope.launch { preferences.setVenues(venues.map(::encodeVenue).toSet()) }
    }

    fun deleteVenue(id: String) {
        venues.removeAll { it.id == id }
        scope.launch { preferences.setVenues(venues.map(::encodeVenue).toSet()) }
    }

    fun markNotificationRead(id: String) {
        readNotificationIds = readNotificationIds + id
        scope.launch { preferences.setReadNotificationIds(readNotificationIds) }
    }

    fun markAllNotificationsRead() {
        readNotificationIds = notificationCatalog.map { it.id }.toSet()
        scope.launch { preferences.setReadNotificationIds(readNotificationIds) }
    }

    fun loadWeather(place: Place, force: Boolean = false) {
        val cached = liveWeather[place.id]
        if (!force && cached != null && System.currentTimeMillis() - cached.updatedAtMillis < 30 * 60 * 1000) return
        if (place.id in weatherLoadingIds) return
        weatherLoadingIds = weatherLoadingIds + place.id
        weatherErrors.remove(place.id)
        scope.launch {
            val result = withContext(Dispatchers.IO) { weatherRepository.fetch(place) }
            result.onSuccess { liveWeather[place.id] = it }
                .onFailure { weatherErrors[place.id] = it.message ?: "Weather unavailable" }
            weatherLoadingIds = weatherLoadingIds - place.id
        }
    }

    private fun encodePost(post: CommunityPost): String = encodeFields(
        post.id,
        post.user,
        post.location.kk,
        post.location.ru,
        post.location.en,
        post.caption.kk,
        post.caption.ru,
        post.caption.en,
        post.image.toString(),
        post.is360.toString(),
        post.verifiedGps.toString(),
        post.mediaType,
    )

    private fun decodePost(value: String): CommunityPost? {
        val f = decodeFields(value)
        if (f.size < 11) return null
        return CommunityPost(
            id = f[0],
            user = f[1],
            location = LocalText(f[2], f[3], f[4]),
            caption = LocalText(f[5], f[6], f[7]),
            image = f[8],
            likes = 0,
            comments = emptyList(),
            is360 = f[9].toBoolean(),
            verifiedGps = f[10].toBoolean(),
            mediaType = f.getOrElse(11) { "image" },
        )
    }

    private fun encodeRoute(route: TravelRoute): String = encodeFields(
        route.id.toString(),
        route.title.kk,
        route.title.ru,
        route.title.en,
        route.image.toString(),
        route.days.toString(),
        route.rating.toString(),
        route.trust.toString(),
        route.price.toString(),
        route.distanceKm.toString(),
        route.interests.joinToString(","),
        route.transport.kk,
        route.transport.ru,
        route.transport.en,
        route.lodging.kk,
        route.lodging.ru,
        route.lodging.en,
        route.pace.kk,
        route.pace.ru,
        route.pace.en,
        route.departureDate,
    )

    private fun decodeRoute(value: String): TravelRoute? {
        val f = decodeFields(value)
        if (f.size < 11) return null
        val image = f[4].toIntOrNull() ?: return null
        val days = f[5].toIntOrNull() ?: return null
        val base = SampleData.routes.firstOrNull { it.image == image } ?: SampleData.routes.first()
        val itinerary = (1..days).map { day ->
            base.itinerary[(day - 1) % base.itinerary.size].copy(day = day)
        }
        return base.copy(
            id = f[0].toIntOrNull() ?: return null,
            title = LocalText(f[1], f[2], f[3]),
            days = days,
            rating = f[6].toDoubleOrNull() ?: base.rating,
            trust = f[7].toIntOrNull() ?: base.trust,
            price = f[8].toIntOrNull() ?: base.price,
            distanceKm = f[9].toIntOrNull() ?: base.distanceKm,
            interests = f[10].split(",").filter(String::isNotBlank).toSet(),
            itinerary = itinerary,
            transport = if (f.size >= 14) LocalText(f[11], f[12], f[13]) else base.transport,
            lodging = if (f.size >= 17) LocalText(f[14], f[15], f[16]) else base.lodging,
            pace = if (f.size >= 20) LocalText(f[17], f[18], f[19]) else base.pace,
            departureDate = f.getOrElse(20) { "" },
        )
    }

    private fun encodeVenue(venue: VenueListing): String = encodeFields(
        venue.id,
        venue.name,
        venue.category,
        venue.address,
        venue.averagePrice.toString(),
        venue.description,
        venue.status,
        venue.imageUri,
    )

    private fun decodeVenue(value: String): VenueListing? {
        val f = decodeFields(value)
        if (f.size < 7) return null
        return VenueListing(
            f[0],
            f[1],
            f[2],
            f[3],
            f[4].toIntOrNull() ?: 0,
            f[5],
            f[6],
            f.getOrElse(7) { "" },
        )
    }

    private fun decodeComment(value: String): Pair<String, String>? {
        val f = decodeFields(value)
        return if (f.size >= 2) f[0] to f[1] else null
    }

    private fun encodeFields(vararg values: String): String = values.joinToString("|") { Uri.encode(it) }
    private fun decodeFields(value: String): List<String> = value.split("|").map(Uri::decode)

    private companion object {
        val notificationCatalog = listOf(
            AppNotification(
                "weather-kolsai",
                LocalText("Көлсайда күн ашық", "На Кольсае ясно", "Clear weather at Kolsai"),
                LocalText("Ертең серуенге қолайлы: +18°C, жел әлсіз.", "Завтра удобно для прогулки: +18°C, слабый ветер.", "Great for a walk tomorrow: 18°C and light wind."),
                LocalText("10 мин бұрын", "10 мин назад", "10 min ago"),
            ),
            AppNotification(
                "route-discount",
                LocalText("Маршрут бағасы түсті", "Цена маршрута снизилась", "Route price dropped"),
                LocalText("Көлсай + Қайыңды бағытына 12% жеңілдік пайда болды.", "На маршрут Кольсай + Каинды появилась скидка 12%.", "Kolsai + Kaindy now has a 12% discount."),
                LocalText("1 сағ бұрын", "1 ч назад", "1 hour ago"),
            ),
            AppNotification(
                "passport",
                LocalText("Passport тапсырмасы", "Задание Passport", "Passport challenge"),
                LocalText("Бір жаңа орынды белгіле де, 250 XP жина.", "Отметь новое место и получи 250 XP.", "Check in at a new place and earn 250 XP."),
                LocalText("Бүгін", "Сегодня", "Today"),
            ),
        )
    }
}

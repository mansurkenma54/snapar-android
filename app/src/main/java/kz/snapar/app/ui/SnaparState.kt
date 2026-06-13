package kz.snapar.app.ui

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kz.snapar.app.data.AppPreferences
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.TravelRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class SnaparState(
    context: Context,
    private val scope: CoroutineScope,
) {
    private val preferences = AppPreferences(context.applicationContext)

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
    var publishedCount by mutableIntStateOf(0)
        private set
    var routeCount by mutableIntStateOf(0)
        private set

    val sessionPosts = mutableStateListOf<CommunityPost>()
    val generatedRoutes = mutableStateListOf<TravelRoute>()

    val xp: Int get() = visitedIds.size * 250 + publishedCount * 75 + routeCount * 50
    val level: Int get() = (xp / 500) + 1

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
                publishedCount = stored.publishedCount
                routeCount = stored.routeCount
                loaded = true
            }
        }
    }

    fun finishOnboarding() {
        onboardingDone = true
        scope.launch { preferences.setOnboardingDone(true) }
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

    fun publish(post: CommunityPost) {
        sessionPosts.add(0, post)
        publishedCount += 1
        scope.launch { preferences.setPublishedCount(publishedCount) }
    }

    fun saveGeneratedRoute(route: TravelRoute) {
        generatedRoutes.add(0, route)
        routeCount += 1
        scope.launch { preferences.setRouteCount(routeCount) }
    }
}

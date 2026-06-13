package kz.snapar.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.snaparDataStore by preferencesDataStore(name = "snapar_preferences")

data class StoredPreferences(
    val onboardingDone: Boolean = false,
    val language: String = "kk",
    val likedIds: Set<String> = emptySet(),
    val dislikedIds: Set<String> = emptySet(),
    val savedIds: Set<String> = emptySet(),
    val visitedIds: Set<String> = emptySet(),
    val notificationsEnabled: Boolean = true,
    val businessMode: Boolean = false,
    val publishedCount: Int = 0,
    val routeCount: Int = 0,
)

class AppPreferences(private val context: Context) {
    private object Keys {
        val onboardingDone = booleanPreferencesKey("onboarding_done")
        val language = stringPreferencesKey("language")
        val likedIds = stringSetPreferencesKey("liked_ids")
        val dislikedIds = stringSetPreferencesKey("disliked_ids")
        val savedIds = stringSetPreferencesKey("saved_ids")
        val visitedIds = stringSetPreferencesKey("visited_ids")
        val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val businessMode = booleanPreferencesKey("business_mode")
        val publishedCount = intPreferencesKey("published_count")
        val routeCount = intPreferencesKey("route_count")
    }

    val values: Flow<StoredPreferences> = context.snaparDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { prefs ->
            StoredPreferences(
                onboardingDone = prefs[Keys.onboardingDone] ?: false,
                language = prefs[Keys.language] ?: "kk",
                likedIds = prefs[Keys.likedIds] ?: emptySet(),
                dislikedIds = prefs[Keys.dislikedIds] ?: emptySet(),
                savedIds = prefs[Keys.savedIds] ?: emptySet(),
                visitedIds = prefs[Keys.visitedIds] ?: emptySet(),
                notificationsEnabled = prefs[Keys.notificationsEnabled] ?: true,
                businessMode = prefs[Keys.businessMode] ?: false,
                publishedCount = prefs[Keys.publishedCount] ?: 0,
                routeCount = prefs[Keys.routeCount] ?: 0,
            )
        }

    suspend fun setOnboardingDone(value: Boolean) = set(Keys.onboardingDone, value)
    suspend fun setLanguage(value: String) = set(Keys.language, value)
    suspend fun setLiked(value: Set<String>) = set(Keys.likedIds, value)
    suspend fun setDisliked(value: Set<String>) = set(Keys.dislikedIds, value)
    suspend fun setSaved(value: Set<String>) = set(Keys.savedIds, value)
    suspend fun setVisited(value: Set<String>) = set(Keys.visitedIds, value)
    suspend fun setNotifications(value: Boolean) = set(Keys.notificationsEnabled, value)
    suspend fun setBusinessMode(value: Boolean) = set(Keys.businessMode, value)
    suspend fun setPublishedCount(value: Int) = set(Keys.publishedCount, value)
    suspend fun setRouteCount(value: Int) = set(Keys.routeCount, value)

    private suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        context.snaparDataStore.edit { it[key] = value }
    }
}

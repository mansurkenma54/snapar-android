package kz.snapar.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppScreen
import kz.snapar.app.model.AppTheme
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.Place
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.SnaparBottomBar
import kz.snapar.app.ui.components.SnaparTopBar
import kz.snapar.app.ui.screens.BookingScreen
import kz.snapar.app.ui.screens.BusinessScreen
import kz.snapar.app.ui.screens.DestinationScreen
import kz.snapar.app.ui.screens.DiscoverScreen
import kz.snapar.app.ui.screens.GeoSnapScreen
import kz.snapar.app.ui.screens.OnboardingScreen
import kz.snapar.app.ui.screens.PassportScreen
import kz.snapar.app.ui.screens.ProfileScreen
import kz.snapar.app.ui.screens.RouteBuilderScreen
import kz.snapar.app.ui.screens.RouteDetailScreen
import kz.snapar.app.ui.screens.RoutesScreen
import kz.snapar.app.ui.screens.SaiScreen
import kz.snapar.app.ui.screens.ShortsScreen
import kz.snapar.app.ui.theme.SnaparTheme

@Composable
fun SnaparApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember { SnaparState(context, scope) }
    val darkTheme = when (state.theme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
    SnaparTheme(darkTheme = darkTheme, largeText = state.largeText) {
        SnaparContent(state)
    }
}

@Composable
private fun SnaparContent(state: SnaparState) {
    val labels = strings(state.language)

    var current by rememberSaveable { mutableStateOf(AppScreen.Discover) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedPost by remember { mutableStateOf<CommunityPost?>(null) }
    var selectedRoute by remember { mutableStateOf<TravelRoute?>(null) }
    var showRouteBuilder by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var bookingPlace by remember { mutableStateOf<Place?>(null) }

    BackHandler(
        enabled = bookingPlace != null ||
            selectedPost != null ||
            selectedPlace != null ||
            selectedRoute != null ||
            showRouteBuilder ||
            showNotifications ||
            current !in listOf(AppScreen.Discover, AppScreen.Routes, AppScreen.Passport, AppScreen.Profile),
    ) {
        when {
            bookingPlace != null -> bookingPlace = null
            showNotifications -> showNotifications = false
            selectedPost != null -> selectedPost = null
            showRouteBuilder -> showRouteBuilder = false
            selectedRoute != null -> selectedRoute = null
            selectedPlace != null -> selectedPlace = null
            else -> current = AppScreen.Discover
        }
    }

    if (!state.loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!state.onboardingDone) {
        OnboardingScreen(
            language = state.language,
            onLanguageChange = state::updateLanguage,
            onFinish = state::finishOnboarding,
        )
        return
    }

    selectedPost?.let { post ->
        val relatedPlace = SampleData.places.firstOrNull { place ->
            place.image == post.image || place.communityImages.any { it == post.image }
        } ?: SampleData.places.firstOrNull { it.id == 4 }
        ShortsScreen(
            initialPost = post,
            posts = state.sessionPosts + SampleData.posts,
            language = state.language,
            state = state,
            onBack = { selectedPost = null },
            onPlace = { selectedPlace = relatedPlace },
            onSai = {
                selectedPost = null
                current = AppScreen.Sai
            },
        )
        return
    }

    if (showRouteBuilder) {
        RouteBuilderScreen(
            language = state.language,
            onBack = { showRouteBuilder = false },
            onGenerated = {
                state.saveGeneratedRoute(it)
                selectedRoute = it
                selectedPlace = null
                showRouteBuilder = false
            },
        )
        return
    }

    bookingPlace?.let { place ->
        BookingScreen(
            place = place,
            language = state.language,
            onBack = { bookingPlace = null },
        )
        return
    }

    selectedPlace?.let { place ->
        DestinationScreen(
            place = place,
            state = state,
            onBack = { selectedPlace = null },
            onOpenShorts = { selectedPost = it },
            onAskSai = {
                selectedPlace = null
                current = AppScreen.Sai
            },
            onBuildRoute = { showRouteBuilder = true },
            onBook = { bookingPlace = place },
        )
        return
    }

    selectedRoute?.let { route ->
        RouteDetailScreen(route = route, state = state, onBack = { selectedRoute = null })
        return
    }

    if (current == AppScreen.Sai) {
        SaiScreen(
            language = state.language,
            messages = state.saiMessages,
            onBack = { current = AppScreen.Discover },
            onRouteGenerated = {
                state.saveGeneratedRoute(it)
                selectedRoute = it
            },
        )
        return
    }

    if (current == AppScreen.GeoSnap) {
        GeoSnapScreen(
            state = state,
            onBack = { current = AppScreen.Discover },
            onOpenShorts = { selectedPost = it },
        )
        return
    }

    if (current == AppScreen.Business) {
        BusinessScreen(state = state, onBack = { current = AppScreen.Profile })
        return
    }

    Scaffold(
        topBar = {
            SnaparTopBar(
                onMenuClick = { current = AppScreen.Profile },
                onNotificationsClick = { showNotifications = true },
                onCameraClick = { current = AppScreen.GeoSnap },
                unreadNotifications = if (state.notificationsEnabled) state.unreadNotifications else 0,
            )
        },
        bottomBar = {
            SnaparBottomBar(
                selected = current,
                labels = labels,
                onSelect = { current = it },
                reducedMotion = state.reducedMotion,
            )
        },
    ) { padding ->
        when (current) {
            AppScreen.Discover -> DiscoverScreen(
                modifier = Modifier.padding(padding),
                state = state,
                onPlace = { selectedPlace = it },
                onRoute = { current = AppScreen.Routes },
                onSai = { current = AppScreen.Sai },
                onGeoSnap = { current = AppScreen.GeoSnap },
                onShorts = { selectedPost = it },
            )
            AppScreen.Routes -> RoutesScreen(
                modifier = Modifier.padding(padding),
                language = state.language,
                routes = state.generatedRoutes + SampleData.routes,
                onRoute = { selectedRoute = it },
                onBuild = { showRouteBuilder = true },
            )
            AppScreen.Passport -> PassportScreen(
                modifier = Modifier.padding(padding),
                state = state,
                onPlace = { selectedPlace = it },
            )
            AppScreen.Profile -> ProfileScreen(
                modifier = Modifier.padding(padding),
                state = state,
                onBusiness = { current = AppScreen.Business },
                onPlace = { selectedPlace = it },
                onRoute = { selectedRoute = it },
            )
            else -> Unit
        }
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text(labels.notifications) },
            text = {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.notifications) { notification ->
                        Card(
                            onClick = { state.markNotificationRead(notification.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f),
                            ),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!notification.isRead) {
                                        Box(
                                            Modifier
                                                .size(7.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                                        )
                                        Spacer(Modifier.width(7.dp))
                                    }
                                    Text(notification.title.value(state.language), fontWeight = FontWeight.Bold)
                                }
                                Text(notification.message.value(state.language), style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    notification.time.value(state.language),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    state.markAllNotificationsRead()
                    showNotifications = false
                }) {
                    Text(
                        when (state.language) {
                            kz.snapar.app.model.AppLanguage.Kazakh -> "Бәрін оқу"
                            kz.snapar.app.model.AppLanguage.Russian -> "Прочитать все"
                            kz.snapar.app.model.AppLanguage.English -> "Mark all read"
                        },
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotifications = false }) { Text("OK") }
            },
        )
    }
}

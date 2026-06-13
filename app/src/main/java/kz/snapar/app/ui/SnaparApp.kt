package kz.snapar.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppScreen
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.Place
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.SnaparBottomBar
import kz.snapar.app.ui.components.SnaparTopBar
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

@Composable
fun SnaparApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember { SnaparState(context, scope) }
    val labels = strings(state.language)

    var current by rememberSaveable { mutableStateOf(AppScreen.Discover) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedPost by remember { mutableStateOf<CommunityPost?>(null) }
    var selectedRoute by remember { mutableStateOf<TravelRoute?>(null) }
    var showRouteBuilder by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

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
        ShortsScreen(
            initialPost = post,
            posts = state.sessionPosts + SampleData.posts,
            language = state.language,
            onBack = { selectedPost = null },
            onPlace = { selectedPlace = SampleData.places.firstOrNull { it.id == 4 } },
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
        )
        return
    }

    selectedRoute?.let { route ->
        RouteDetailScreen(route = route, language = state.language, onBack = { selectedRoute = null })
        return
    }

    if (current == AppScreen.Sai) {
        SaiScreen(
            language = state.language,
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
        BusinessScreen(language = state.language, onBack = { current = AppScreen.Profile })
        return
    }

    Scaffold(
        topBar = {
            SnaparTopBar(
                onMenuClick = { current = AppScreen.Profile },
                onNotificationsClick = { showNotifications = true },
                onCameraClick = { current = AppScreen.GeoSnap },
            )
        },
        bottomBar = {
            SnaparBottomBar(
                selected = current,
                labels = labels,
                onSelect = { current = it },
            )
        },
    ) { padding ->
        when (current) {
            AppScreen.Discover -> DiscoverScreen(
                modifier = Modifier.padding(padding),
                state = state,
                onPlace = { selectedPlace = it },
                onRoute = {
                    current = AppScreen.Routes
                    selectedRoute = SampleData.routes.firstOrNull()
                },
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
            )
            else -> Unit
        }
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text(labels.notifications) },
            text = {
                Text(
                    when (state.language) {
                        kz.snapar.app.model.AppLanguage.Kazakh -> "Көлсайда ертең ауа райы ашық. Сақталған маршрутыңызға 12% жеңілдік пайда болды."
                        kz.snapar.app.model.AppLanguage.Russian -> "Завтра на Кольсае ясно. Для сохранённого маршрута появилась скидка 12%."
                        kz.snapar.app.model.AppLanguage.English -> "Clear weather at Kolsai tomorrow. Your saved route now has a 12% discount."
                    },
                )
            },
            confirmButton = {
                TextButton(onClick = { showNotifications = false }) { Text("OK") }
            },
        )
    }
}

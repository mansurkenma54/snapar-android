package kz.snapar.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.MotionPhotosOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kz.snapar.app.R
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.AppTheme
import kz.snapar.app.model.Place
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparGold
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun ProfileScreen(
    modifier: Modifier,
    state: SnaparState,
    onBusiness: () -> Unit,
    onPlace: (Place) -> Unit,
    onRoute: (TravelRoute) -> Unit,
) {
    val labels = strings(state.language)
    val saved = SampleData.places.filter { it.id in state.savedIds }
    val savedRoutes = (state.generatedRoutes + SampleData.routes).filter { it.id in state.savedRouteIds }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        state.setNotifications(granted)
    }
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .size(126.dp)
                        .background(SnaparTurquoise.copy(.35f), CircleShape)
                        .padding(10.dp),
                ) {
                    AsyncImage(R.drawable.profile, "Profile", Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                }
                Spacer(Modifier.size(10.dp))
                Text("Азамат Н.", style = MaterialTheme.typography.titleLarge)
                Text("Steppe Explorer & Urban Nomad", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${state.level} level · ${state.xp} XP", color = SnaparPrimary, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileStat(Icons.Outlined.LocationOn, "${state.visitedIds.size}", profileLocal(state.language, "Орын", "Места", "Places"), Modifier.weight(1f))
                ProfileStat(Icons.Outlined.Route, "${state.traveledKm}", "км", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileStat(Icons.Outlined.CameraAlt, "${state.publishedCount}", "GeoSnap", Modifier.weight(1f))
                ProfileStat(Icons.Outlined.Route, "${state.routeCount}", profileLocal(state.language, "Маршрут", "Маршруты", "Routes"), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileStat(Icons.Outlined.Favorite, "${state.likedIds.size + state.likedPostIds.size}", profileLocal(state.language, "Ұнату", "Лайки", "Likes"), Modifier.weight(1f))
                ProfileStat(Icons.Outlined.Settings, "${state.xp}", "XP", Modifier.weight(1f))
            }
        }
        item {
            SettingRow(Icons.Outlined.BusinessCenter, labels.businessMode) {
                Switch(checked = state.businessMode, onCheckedChange = state::updateBusinessMode)
            }
            if (state.businessMode) {
                Button(onClick = onBusiness, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(profileLocal(state.language, "Бизнес панелін ашу", "Открыть бизнес-панель", "Open business dashboard"))
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Language, null, tint = SnaparPrimary)
                    Spacer(Modifier.width(10.dp))
                    Text(labels.language, fontWeight = FontWeight.Bold)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AppLanguage.entries) { language ->
                        FilterChip(
                            selected = state.language == language,
                            onClick = { state.updateLanguage(language) },
                            label = { Text(language.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SnaparTurquoise,
                                selectedLabelColor = Color.White,
                            ),
                        )
                    }
                }
            }
        }
        item {
            SettingRow(Icons.Outlined.Notifications, labels.notifications) {
                Switch(
                    checked = state.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            state.setNotifications(enabled)
                        }
                    },
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DarkMode, null, tint = SnaparPrimary)
                    Spacer(Modifier.width(10.dp))
                    Text(profileLocal(state.language, "Тақырып", "Тема", "Theme"), fontWeight = FontWeight.Bold)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AppTheme.entries) { theme ->
                        FilterChip(
                            selected = state.theme == theme,
                            onClick = { state.updateTheme(theme) },
                            label = {
                                Text(
                                    when (theme) {
                                        AppTheme.System -> profileLocal(state.language, "Жүйе", "Система", "System")
                                        AppTheme.Light -> profileLocal(state.language, "Жарық", "Светлая", "Light")
                                        AppTheme.Dark -> profileLocal(state.language, "Қараңғы", "Тёмная", "Dark")
                                    },
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SnaparTurquoise,
                                selectedLabelColor = Color.White,
                            ),
                        )
                    }
                }
            }
        }
        item {
            SettingRow(Icons.Outlined.FormatSize, profileLocal(state.language, "Үлкен мәтін", "Крупный текст", "Large text")) {
                Switch(checked = state.largeText, onCheckedChange = state::updateLargeText)
            }
        }
        item {
            SettingRow(Icons.Outlined.MotionPhotosOff, profileLocal(state.language, "Анимацияны азайту", "Уменьшить анимацию", "Reduce motion")) {
                Switch(checked = state.reducedMotion, onCheckedChange = state::updateReducedMotion)
            }
        }
        if (saved.isNotEmpty()) {
            item { SectionTitle(labels.savedPlaces) }
            items(saved) { place ->
                Card(
                    Modifier.fillMaxWidth().clickable { onPlace(place) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(place.image, place.name.value(state.language), Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(place.name.value(state.language), fontWeight = FontWeight.Bold)
                            Text(place.region.value(state.language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Outlined.Favorite, null, tint = SnaparGold)
                    }
                }
            }
        }
        if (savedRoutes.isNotEmpty()) {
            item { SectionTitle(profileLocal(state.language, "Сақталған маршруттар", "Сохранённые маршруты", "Saved routes")) }
            items(savedRoutes) { route ->
                Card(
                    Modifier.fillMaxWidth().clickable { onRoute(route) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(route.image, route.title.value(state.language), Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(route.title.value(state.language), fontWeight = FontWeight.Bold)
                            Text("${route.days} күн · ${route.price} ₸", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = SnaparTurquoise, modifier = Modifier.size(20.dp))
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable () -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(15.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = SnaparPrimary)
            Spacer(Modifier.width(10.dp))
            Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            trailing()
        }
    }
}

private fun profileLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

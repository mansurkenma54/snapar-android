package kz.snapar.app.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.TempleBuddhist
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import kz.snapar.app.model.Place
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun PassportScreen(
    modifier: Modifier,
    state: SnaparState,
    onPlace: (Place) -> Unit,
) {
    val language = state.language
    val levelXp = state.xp % 500
    val levelTitle = when {
        state.level >= 12 -> passportLocal(language, "Зерттеуші", "Исследователь", "Explorer")
        state.level >= 6 -> passportLocal(language, "Саяхатшы", "Путешественник", "Traveler")
        else -> passportLocal(language, "Жол бастаушы", "Новичок", "Trail Starter")
    }
    val visited = SampleData.places.filter { it.id in state.visitedIds }
    val achievements = listOf(
        Achievement(Icons.Outlined.Waves, passportLocal(language, "Көлсай Explorer", "Kolsai Explorer", "Kolsai Explorer"), 1 in state.visitedIds),
        Achievement(Icons.Outlined.Landscape, passportLocal(language, "Шатқал Adventurer", "Canyon Adventurer", "Canyon Adventurer"), 4 in state.visitedIds),
        Achievement(Icons.Outlined.TempleBuddhist, passportLocal(language, "Жасырын орын", "Скрытое место", "Hidden place"), state.visitedIds.any { it >= 5 }),
        Achievement(Icons.Outlined.Route, passportLocal(language, "Маршрут шебері", "Мастер маршрута", "Route maker"), state.routeCount >= 1),
        Achievement(Icons.Outlined.Image, "GeoSnap Creator", state.publishedCount >= 1),
        Achievement(Icons.Outlined.RocketLaunch, "Қазақстан 17", state.visitedIds.size >= 17),
    )

    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${state.level}-${passportLocal(language, "деңгей", "уровень", "level")} — $levelTitle", style = MaterialTheme.typography.titleLarge)
                        Text("${state.xp} XP", color = SnaparPrimary, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { levelXp / 500f },
                        modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                        color = SnaparTurquoise,
                    )
                    Text("${500 - levelXp} XP ${passportLocal(language, "келесі деңгейге дейін", "до следующего уровня", "to next level")}", color = SnaparMuted, fontSize = 12.sp)
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(passportLocal(language, "Қазақстан картасы", "Карта Казахстана", "Map of Kazakhstan"))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(275.dp)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AsyncImage(R.drawable.passport_map, "Kazakhstan", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart).padding(14.dp),
                        color = Color.White.copy(.92f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            "${passportLocal(language, "Ашылған аймақтар", "Открытые регионы", "Unlocked regions")}: ${state.visitedIds.size} / 17",
                            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Icons.Outlined.LocationOn, "${state.visitedIds.size}", passportLocal(language, "ОРЫНДАР", "МЕСТА", "PLACES"), Modifier.weight(1f))
                StatTile(Icons.Outlined.Route, "${state.routeCount}", passportLocal(language, "МАРШРУТ", "МАРШРУТЫ", "ROUTES"), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Icons.Outlined.Image, "${state.publishedCount}", passportLocal(language, "GEOSNAP", "GEOSNAP", "GEOSNAPS"), Modifier.weight(1f))
                StatTile(Icons.Outlined.FavoriteBorder, "${state.likedIds.size}", passportLocal(language, "ҰНАТУ", "ЛАЙКИ", "LIKES"), Modifier.weight(1f))
            }
        }
        item { SectionTitle(passportLocal(language, "Жетістіктер", "Достижения", "Achievements")) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(achievements) { achievement ->
                    AchievementCard(achievement)
                }
            }
        }
        if (visited.isNotEmpty()) {
            item { SectionTitle(passportLocal(language, "Барған орындар", "Посещённые места", "Visited places")) }
            items(visited) { place ->
                Card(
                    Modifier.fillMaxWidth().clickable { onPlace(place) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(place.image, place.name.value(language), Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(place.name.value(language), fontWeight = FontWeight.Bold)
                            Text(place.region.value(language), color = SnaparMuted)
                        }
                    }
                }
            }
        }
    }
}

private data class Achievement(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val unlocked: Boolean,
)

@Composable
private fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.width(145.dp).alpha(if (achievement.unlocked) 1f else .48f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(58.dp).background(if (achievement.unlocked) SnaparTurquoise else Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                Icon(achievement.icon, null, tint = if (achievement.unlocked) SnaparNavy else Color.Gray)
            }
            Text(achievement.title, fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, minLines = 2)
        }
    }
}

@Composable
private fun StatTile(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(17.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(value, color = SnaparNavy, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Text(label, color = SnaparMuted, fontSize = 11.sp)
        }
    }
}

private fun passportLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

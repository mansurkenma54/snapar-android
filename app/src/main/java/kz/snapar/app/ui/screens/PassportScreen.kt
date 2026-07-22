package kz.snapar.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import kz.snapar.app.ui.components.pressScale
import kz.snapar.app.ui.theme.SnaparGold
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
        state.level >= 15 -> "Legend"
        state.level >= 10 -> "Discoverer"
        state.level >= 7 -> "Traveler"
        state.level >= 4 -> "Nomad"
        else -> "Explorer"
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
            // Level card — gradient + animated XP bar
            var xpVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { xpVisible = true }
            val xpProgress by animateFloatAsState(
                targetValue = if (xpVisible) levelXp / 500f else 0f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing),
                label = "xp-anim",
            )
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(SnaparTurquoise.copy(.05f), SnaparPrimary.copy(.08f)),
                            ),
                        )
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(
                                    passportLocal(language, "$levelTitle Саяхатшы", "$levelTitle Путешественник", "$levelTitle Traveler"),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = SnaparNavy,
                                )
                                Text(
                                    "${passportLocal(language, "Деңгей", "Уровень", "Level")} ${state.level}",
                                    color = SnaparTurquoise,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                            Surface(
                                color = SnaparGold.copy(.15f),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Row(
                                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(Icons.Outlined.EmojiEvents, null, tint = SnaparGold, modifier = Modifier.size(18.dp))
                                    Text("${state.xp} XP", color = SnaparGold, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                            }
                        }
                        // Animated progress bar
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth(xpProgress)
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(listOf(SnaparTurquoise, SnaparPrimary)),
                                        ),
                                )
                            }
                            Text(
                                "${(xpProgress * 500).toInt()} / 500 XP — ${500 - levelXp} ${passportLocal(language, "қалды", "осталось", "to go")}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(passportLocal(language, "Қазақстан картасы", "Карта Казахстана", "Map of Kazakhstan"))
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .height(275.dp)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AsyncImage(R.drawable.passport_map, "Kazakhstan", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    visited.forEach { place ->
                        val xFraction = ((place.longitude - 46.0) / 41.0).toFloat().coerceIn(.04f, .94f)
                        val yFraction = ((55.0 - place.latitude) / 15.0).toFloat().coerceIn(.08f, .88f)
                        Box(
                            Modifier
                                .offset(
                                    x = maxWidth * xFraction - 10.dp,
                                    y = maxHeight * yFraction - 10.dp,
                                )
                                .size(22.dp)
                                .background(SnaparTurquoise, CircleShape)
                                .clickable { onPlace(place) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(Modifier.size(7.dp).background(Color.White, CircleShape))
                        }
                    }
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
                    Modifier.fillMaxWidth().pressScale(0.98f).clickable { onPlace(place) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            place.image,
                            place.name.value(language),
                            Modifier.size(76.dp).clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(place.name.value(language), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(place.region.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Surface(color = SnaparTurquoise.copy(.10f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "✓ ${passportLocal(language, "Барылды", "Посещено", "Visited")}",
                                    Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = SnaparTurquoise,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
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
    val infinite = rememberInfiniteTransition(label = "achieve")
    val glowScale by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow",
    )
    Card(
        modifier = Modifier
            .width(148.dp)
            .alpha(if (achievement.unlocked) 1f else .45f)
            .pressScale(enabled = achievement.unlocked),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(if (achievement.unlocked) 3.dp else 1.dp),
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (achievement.unlocked) {
                    // Glow ring
                    Box(
                        Modifier
                            .scale(glowScale)
                            .size(64.dp)
                            .background(
                                Brush.radialGradient(listOf(SnaparTurquoise.copy(.25f), Color.Transparent)),
                                CircleShape,
                            ),
                    )
                }
                Box(
                    Modifier
                        .size(52.dp)
                        .background(
                            if (achievement.unlocked) Brush.linearGradient(listOf(SnaparTurquoise, SnaparPrimary))
                            else Brush.linearGradient(listOf(Color.LightGray, Color.Gray)),
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(achievement.icon, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
            Text(
                achievement.title,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                minLines = 2,
                fontSize = 13.sp,
            )
            if (achievement.unlocked) {
                Surface(color = SnaparTurquoise.copy(.12f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        "✓",
                        Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        color = SnaparTurquoise,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(SnaparTurquoise.copy(.04f), Color.Transparent)))
                .padding(18.dp),
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(SnaparTurquoise.copy(.12f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, null, tint = SnaparTurquoise, modifier = Modifier.size(22.dp))
                }
                Text(value, color = SnaparNavy, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun passportLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

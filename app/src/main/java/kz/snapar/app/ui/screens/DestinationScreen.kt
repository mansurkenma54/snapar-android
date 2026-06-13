package kz.snapar.app.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.Place
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparGold
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun DestinationScreen(
    place: Place,
    state: SnaparState,
    onBack: () -> Unit,
    onOpenShorts: (CommunityPost) -> Unit,
    onAskSai: () -> Unit,
    onBuildRoute: () -> Unit,
) {
    val language = state.language
    val labels = strings(language)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var selectedDay by remember { mutableIntStateOf(SampleData.calendarOffers.first().day) }
    val isSaved = place.id in state.savedIds
    val isVisited = place.id in state.visitedIds

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = Color.White) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconButton(
                        onClick = { state.toggleSaved(place.id) },
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFFE9F0EF), RoundedCornerShape(15.dp)),
                    ) {
                        Icon(
                            if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = labels.saved,
                            tint = SnaparPrimary,
                        )
                    }
                    Button(
                        onClick = onAskSai,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text(labels.askSai, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(430.dp),
                ) {
                    AsyncImage(
                        model = place.image,
                        contentDescription = place.name.value(language),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationY = listState.firstVisibleItemScrollOffset * 0.28f
                                scaleX = 1.06f
                                scaleY = 1.06f
                            },
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x33000000), Color.Transparent, Color(0xD7001723)),
                                ),
                            ),
                    )
                    RoundAction(Icons.Outlined.ArrowBack, "Back", Modifier.align(Alignment.TopStart).padding(18.dp), onBack)
                    RoundAction(
                        Icons.Outlined.Share,
                        "Share",
                        Modifier.align(Alignment.TopEnd).padding(18.dp),
                    ) {
                        val text = "${place.name.value(language)} — ${place.region.value(language)}"
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                },
                                "Snapar",
                            ),
                        )
                    }
                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            place.name.value(language),
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text(place.region.value(language), color = Color.White, fontSize = 17.sp)
                        }
                    }
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    item { InfoPill(Icons.Rounded.Star, place.rating.toString(), SnaparGold) }
                    item { InfoPill(Icons.Rounded.AutoAwesome, "${place.trustScore}% Trust", SnaparTurquoise) }
                    item { InfoPill(Icons.Outlined.Cloud, "${place.weather.temperature}°C", SnaparPrimary) }
                    item { InfoPill(Icons.Outlined.LocationOn, "${place.distanceKm} км", SnaparPrimary) }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(labels.about)
                    Text(place.description.value(language), style = MaterialTheme.typography.bodyLarge, color = SnaparMuted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(place.tags) { tag ->
                            Surface(color = SnaparTurquoise.copy(alpha = 0.13f), shape = RoundedCornerShape(20.dp)) {
                                Text(
                                    tag.value(language),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    color = SnaparPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(labels.weather)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WeatherCard(Icons.Outlined.WaterDrop, "${place.weather.humidity}%", local(language, "Ылғал", "Влажность", "Humidity"), Modifier.weight(1f))
                        WeatherCard(Icons.Outlined.Air, "${place.weather.windKmh} км/с", local(language, "Жел", "Ветер", "Wind"), Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WeatherCard(Icons.Outlined.Speed, "${place.weather.pressure} hPa", local(language, "Қысым", "Давление", "Pressure"), Modifier.weight(1f))
                        WeatherCard(Icons.Outlined.Cloud, "${place.weather.sunrise} / ${place.weather.sunset}", local(language, "Күн шығуы/батуы", "Восход/закат", "Sunrise/sunset"), Modifier.weight(1f))
                    }
                }
            }

            item {
                Row(
                    Modifier.padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DetailCard(
                        Icons.Outlined.CalendarMonth,
                        labels.bestSeason,
                        place.bestSeason.value(language),
                        Modifier.weight(1f),
                    )
                    DetailCard(
                        Icons.Outlined.Payments,
                        labels.averageCost,
                        formatTenge(place.averageCost),
                        Modifier.weight(1f),
                    )
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(labels.calendar)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(SampleData.calendarOffers) { offer ->
                            val active = selectedDay == offer.day
                            Card(
                                modifier = Modifier
                                    .width(128.dp)
                                    .clickable { selectedDay = offer.day },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (active) SnaparPrimary else Color.White,
                                ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    Modifier.padding(13.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Text(
                                        "${offer.day} ${local(language, "маусым", "июня", "June")}",
                                        color = if (active) Color.White else SnaparNavy,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "${offer.temperature}° · ${offer.condition.value(language)}",
                                        color = if (active) Color.White else SnaparMuted,
                                        fontSize = 12.sp,
                                    )
                                    HorizontalDivider(color = if (active) Color.White.copy(.3f) else Color(0x16000000))
                                    Text(
                                        formatTenge(offer.hotelPrice),
                                        color = if (active) Color.White else SnaparPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Groups,
                                            null,
                                            tint = if (active) Color.White else SnaparMuted,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            " ${offer.crowdPercent}%",
                                            color = if (active) Color.White else SnaparMuted,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle(labels.community)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(place.communityImages) { image ->
                            val post = CommunityPost(
                                id = "place-${place.id}-$image",
                                user = "snapar.traveler",
                                location = place.region,
                                caption = place.description,
                                image = image,
                                likes = 420 + place.id * 73,
                                comments = listOf(local(language, "Керемет орын!", "Красивое место!", "Beautiful place!")),
                            )
                            AsyncImage(
                                image,
                                place.name.value(language),
                                Modifier
                                    .size(width = 182.dp, height = 156.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .clickable { onOpenShorts(post) },
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val added = state.checkIn(place.id)
                            scope.launch {
                                snackbar.showSnackbar(if (added) labels.checkedIn else local(language, "Бұл орын бұрын белгіленген", "Место уже отмечено", "Already checked in"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isVisited,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(Icons.Rounded.CheckCircle, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isVisited) labels.checkedIn else labels.checkIn)
                    }
                    Button(
                        onClick = onBuildRoute,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SnaparNavy),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text(labels.routeBuilder)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(50.dp)
            .background(Color.White.copy(alpha = .9f), CircleShape),
    ) {
        Icon(icon, description, tint = SnaparNavy)
    }
}

@Composable
private fun InfoPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Surface(color = Color.White, shadowElevation = 1.dp, shape = RoundedCornerShape(22.dp)) {
        Row(
            Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
            Text(text, fontWeight = FontWeight.SemiBold, color = SnaparNavy)
        }
    }
}

@Composable
private fun WeatherCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(value, fontWeight = FontWeight.Bold, color = SnaparNavy)
            Text(label, color = SnaparMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun DetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(label, color = SnaparMuted, fontSize = 12.sp)
            Text(value, color = SnaparNavy, fontWeight = FontWeight.Bold)
        }
    }
}

private fun local(language: AppLanguage, kk: String, ru: String, en: String): String = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

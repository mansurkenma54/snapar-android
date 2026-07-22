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
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PriceCheck
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kz.snapar.app.data.WeatherRepository
import kz.snapar.app.model.CalendarOffer
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.FairnessVerdict
import kz.snapar.app.model.PriceFairness
import kz.snapar.app.model.Place
import kz.snapar.app.model.Review
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.components.formatMinutes
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
    onBook: () -> Unit = {},
) {
    val language = state.language
    val labels = strings(language)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val liveWeather = state.liveWeather[place.id]
    val reviews = remember(place.id) { SampleData.reviewsFor(place.id) }
    val priceFairness = remember(place.id) { SampleData.priceFairnessFor(place) }
    val weather = liveWeather?.current ?: place.weather
    val calendarOffers = liveWeather?.daily?.mapIndexed { index, forecast ->
        CalendarOffer(
            day = forecast.date.takeLast(2).toIntOrNull() ?: (index + 1),
            temperature = forecast.temperatureMax,
            condition = WeatherRepository.weatherCondition(forecast.weatherCode),
            hotelPrice = (place.averageCost * (0.78 + index * 0.045)).toInt(),
            crowdPercent = (32 + forecast.temperatureMax * 2 - forecast.precipitationPercent / 3).coerceIn(18, 94),
        )
    }.orEmpty().ifEmpty { SampleData.calendarOffers }
    var selectedDay by remember(place.id) { mutableIntStateOf(calendarOffers.first().day) }
    val selectedOffer = calendarOffers.firstOrNull { it.day == selectedDay } ?: calendarOffers.first()
    val isSaved = place.id in state.savedIds
    val isVisited = place.id in state.visitedIds

    LaunchedEffect(place.id) {
        state.loadWeather(place)
    }
    LaunchedEffect(calendarOffers.first().day) {
        if (calendarOffers.none { it.day == selectedDay }) selectedDay = calendarOffers.first().day
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        Spacer(Modifier.width(6.dp))
                        Text(labels.askSai, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onBook,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SnaparNavy),
                    ) {
                        Icon(Icons.Outlined.ConfirmationNumber, null)
                        Spacer(Modifier.width(6.dp))
                        Text(labels.booking, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 12.sp)
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
                    Row(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        RoundAction(
                            if (isSaved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                            labels.saved,
                            Modifier,
                        ) { state.toggleSaved(place.id) }
                        RoundAction(
                            Icons.Rounded.CheckCircle,
                            labels.checkIn,
                            Modifier,
                        ) {
                            val added = state.checkIn(place.id)
                            scope.launch {
                                snackbar.showSnackbar(
                                    if (added) labels.checkedIn
                                    else local(language, "Бұл орын бұрын белгіленген", "Место уже отмечено", "Already checked in"),
                                )
                            }
                        }
                        RoundAction(Icons.Outlined.Share, "Share", Modifier) {
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
                    item { InfoPill(Icons.Outlined.Cloud, "${weather.temperature}°C", SnaparPrimary) }
                    item { InfoPill(Icons.Outlined.LocationOn, "${place.distanceKm} км", SnaparPrimary) }
                    item { InfoPill(Icons.Outlined.CalendarMonth, formatMinutes(place.travelMinutes), SnaparPrimary) }
                    item { InfoPill(Icons.Outlined.Speed, place.difficulty.value(language), SnaparGold) }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(labels.about)
                    Text(place.description.value(language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "${weather.temperature}° · ${weather.condition.value(language)}",
                            color = SnaparPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        when {
                            place.id in state.weatherLoadingIds -> CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                            state.weatherErrors[place.id] != null -> IconButton(onClick = { state.loadWeather(place, force = true) }) {
                                Icon(Icons.Outlined.Refresh, local(language, "Қайта жүктеу", "Обновить", "Retry"), tint = SnaparPrimary)
                            }
                            else -> Text(local(language, "Тікелей болжам", "Онлайн-прогноз", "Live forecast"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WeatherCard(Icons.Outlined.WaterDrop, "${weather.humidity}%", local(language, "Ылғал", "Влажность", "Humidity"), Modifier.weight(1f))
                        WeatherCard(Icons.Outlined.Air, "${weather.windKmh} км/с", local(language, "Жел", "Ветер", "Wind"), Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        WeatherCard(Icons.Outlined.Speed, "${weather.pressure} hPa", local(language, "Қысым", "Давление", "Pressure"), Modifier.weight(1f))
                        WeatherCard(Icons.Outlined.Cloud, "${weather.sunrise} / ${weather.sunset}", local(language, "Күн шығуы/батуы", "Восход/закат", "Sunrise/sunset"), Modifier.weight(1f))
                    }
                    Text(
                        "Weather data: Open-Meteo",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                    )
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
                Column(
                    Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SectionTitle(local(language, "Бюджет бөлінісі", "Структура бюджета", "Budget breakdown"))
                    BudgetLine(
                        local(language, "Көлік", "Транспорт", "Transport"),
                        (place.averageCost * .38f).toInt(),
                        .38f,
                    )
                    BudgetLine(
                        local(language, "Тамақ", "Питание", "Food"),
                        (place.averageCost * .27f).toInt(),
                        .27f,
                    )
                    BudgetLine(
                        local(language, "Қону және билет", "Проживание и билеты", "Lodging and tickets"),
                        (place.averageCost * .35f).toInt(),
                        .35f,
                    )
                }
            }

            item {
                PriceFairnessCard(priceFairness, language, labels.priceAnalysis, Modifier.padding(horizontal = 18.dp))
            }

            if (reviews.isNotEmpty()) {
                item {
                    Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val gpsCount = reviews.count { it.gpsVerified }
                        SectionTitle("${labels.reviews} · ${reviews.size}")
                        Surface(
                            color = SnaparTurquoise.copy(.08f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.GppGood, null, tint = SnaparTurquoise, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        local(language, "$gpsCount GPS-расталған", "$gpsCount GPS-подтверждённых", "$gpsCount GPS-verified"),
                                        color = SnaparPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                    )
                                }
                                Text(
                                    local(language, "${reviews.size} пікірдің ішінен", "из ${reviews.size} отзывов", "of ${reviews.size} reviews"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                        reviews.forEach { review ->
                            ReviewCard(review, language, labels.gpsVerifiedBadge)
                        }
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(labels.calendar)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(calendarOffers) { offer ->
                            val active = selectedDay == offer.day
                            Card(
                                modifier = Modifier
                                    .width(128.dp)
                                    .clickable { selectedDay = offer.day },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (active) SnaparPrimary else MaterialTheme.colorScheme.surface,
                                ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    Modifier.padding(13.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Text(
                                        "${offer.day} ${local(language, "маусым", "июня", "June")}",
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        "${offer.temperature}° · ${offer.condition.value(language)}",
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                            tint = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Text(
                                            " ${offer.crowdPercent}%",
                                            color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Surface(
                        color = SnaparTurquoise.copy(alpha = .1f),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(selectedOffer.condition.value(language), fontWeight = FontWeight.Bold)
                                Text("${selectedOffer.temperature}°C", color = SnaparPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${local(language, "Қонақ үй", "Отель", "Hotel")}: ${formatTenge(selectedOffer.hotelPrice)}")
                                Text("${local(language, "Адам көптігі", "Загруженность", "Crowd")}: ${selectedOffer.crowdPercent}%", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp, shape = RoundedCornerShape(22.dp)) {
        Row(
            Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(17.dp))
            Text(text, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
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
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
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
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BudgetLine(label: String, amount: Int, progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatTenge(amount), fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape),
            color = SnaparTurquoise,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun PriceFairnessCard(fairness: PriceFairness, language: AppLanguage, title: String, modifier: Modifier = Modifier) {
    val (verdictLabel, verdictColor) = when (fairness.verdict) {
        FairnessVerdict.Cheap -> Pair(local(language, "Арзан", "Дёшево", "Cheap"), Color(0xFF27AE60))
        FairnessVerdict.Fair -> Pair(local(language, "Ақылға қонымды", "Справедливая", "Fair price"), SnaparTurquoise)
        FairnessVerdict.Overpriced -> Pair(local(language, "Жоғарылау", "Завышена", "Slightly high"), SnaparGold)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.PriceCheck, null, tint = verdictColor, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.Bold)
                }
                Surface(color = verdictColor.copy(.12f), shape = RoundedCornerShape(10.dp)) {
                    Text(verdictLabel, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = verdictColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(local(language, "Нарық орт.", "Среднее рынка", "Market avg"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text(formatTenge(fairness.marketAvg), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Box(Modifier.size(1.dp, 36.dp).background(MaterialTheme.colorScheme.surfaceVariant))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(local(language, "Trust Score", "Trust Score", "Trust Score"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text("${fairness.score}%", fontWeight = FontWeight.Bold, color = verdictColor, fontSize = 14.sp)
                }
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(fairness.score / 100f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(verdictColor),
                )
            }
            Text(fairness.tip.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ReviewCard(review: Review, language: AppLanguage, gpsLabel: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(34.dp)
                            .background(SnaparTurquoise.copy(.15f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(review.user.first().uppercaseChar().toString(), fontWeight = FontWeight.Bold, color = SnaparPrimary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(review.user, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(review.date.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        repeat(5) { i ->
                            Icon(
                                Icons.Rounded.Star,
                                null,
                                tint = if (i < review.rating.toInt()) SnaparGold else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    if (review.gpsVerified) {
                        Surface(color = SnaparTurquoise.copy(.12f), shape = RoundedCornerShape(6.dp)) {
                            Row(
                                Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Outlined.GppGood, null, tint = SnaparTurquoise, modifier = Modifier.size(11.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(gpsLabel, color = SnaparTurquoise, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            Text(review.text.value(language), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ThumbUp, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${review.helpful}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }
    }
}

private fun local(language: AppLanguage, kk: String, ru: String, en: String): String = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

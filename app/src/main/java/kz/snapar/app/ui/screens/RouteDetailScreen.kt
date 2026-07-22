package kz.snapar.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.theme.SnaparGold
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun RouteDetailScreen(route: TravelRoute, state: SnaparState, onBack: () -> Unit) {
    val language = state.language
    val context = LocalContext.current
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(330.dp),
            ) {
                AsyncImage(route.image, route.title.value(language), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x44000000), Color.Transparent, Color(0xD0001723)))))
                HeaderButton(Icons.Outlined.ArrowBack, Modifier.align(Alignment.TopStart).padding(18.dp), onBack)
                Row(Modifier.align(Alignment.TopEnd).padding(18.dp)) {
                    HeaderButton(
                        if (route.id in state.savedRouteIds) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                        Modifier,
                    ) { state.toggleRouteSaved(route) }
                    Spacer(Modifier.width(8.dp))
                    HeaderButton(Icons.Outlined.Share, Modifier) {
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${route.title.value(language)} · ${route.days} days · ${formatTenge(route.price)}")
                                },
                                "Snapar",
                            ),
                        )
                    }
                }
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Text(route.title.value(language), style = MaterialTheme.typography.displayLarge, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${route.days} ${routeLocal(language, "күн", "дня", "days")}", color = Color.White)
                        Text("${route.distanceKm} км", color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, null, tint = SnaparGold, modifier = Modifier.size(16.dp))
                            Text(" ${route.rating}", color = Color.White)
                        }
                    }
                }
            }
        }
        item {
            Row(
                Modifier.padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SummaryBox(routeLocal(language, "Болжамды бюджет", "Бюджет", "Estimated budget"), formatTenge(route.price), Modifier.weight(1f))
                SummaryBox("Trust", "${route.trust}%", Modifier.weight(1f))
            }
        }
        if (route.transport.kk.isNotBlank() || route.departureDate.isNotBlank()) {
            item {
                Card(
                    Modifier.padding(horizontal = 18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(routeLocal(language, "Сапар параметрлері", "Параметры поездки", "Trip settings"), fontWeight = FontWeight.Bold)
                        if (route.departureDate.isNotBlank()) Text("📅 ${route.departureDate}")
                        if (route.transport.kk.isNotBlank()) Text("🚌 ${route.transport.value(language)}")
                        if (route.lodging.kk.isNotBlank()) Text("🏨 ${route.lodging.value(language)}")
                        if (route.pace.kk.isNotBlank()) Text("⚡ ${route.pace.value(language)}")
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp)) {
                SectionTitle(routeLocal(language, "Күндік жоспар", "План по дням", "Daily itinerary"))
            }
        }
        route.itinerary.forEach { day ->
            item {
                Column(
                    Modifier.padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        "${day.day}-${routeLocal(language, "күн", "й день", "day")}: ${day.title.value(language)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    day.stops.forEach { stop ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                                Surface(color = SnaparTurquoise.copy(.16f), shape = CircleShape) {
                                    Icon(
                                        Icons.Outlined.Schedule,
                                        null,
                                        tint = SnaparPrimary,
                                        modifier = Modifier.padding(9.dp),
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(stop.time, color = SnaparTurquoise, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(stop.title.value(language), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(stop.note.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                                if (stop.cost > 0) Text(formatTenge(stop.cost), color = SnaparPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(
                Modifier.padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = { state.toggleRouteSaved(route) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                ) {
                    Icon(if (route.id in state.savedRouteIds) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder, null)
                    Spacer(Modifier.width(6.dp))
                    Text(routeLocal(language, "Сақтау", "Сохранить", "Save"))
                }
                Button(
                    onClick = {
                        val destination = if (route.id == 102) "43.350,79.080" else "42.991,78.325"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$destination?q=$destination")))
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SnaparNavy),
                ) {
                    Icon(Icons.Outlined.Navigation, null)
                    Spacer(Modifier.width(6.dp))
                    Text(routeLocal(language, "Жолды ашу", "Открыть путь", "Navigate"))
                }
            }
        }
    }
}

@Composable
private fun HeaderButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(50.dp)
            .background(Color.White.copy(.9f), CircleShape),
    ) {
        Icon(icon, null, tint = SnaparNavy)
    }
}

@Composable
private fun SummaryBox(label: String, value: String, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(value, color = SnaparPrimary, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        }
    }
}

private fun routeLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

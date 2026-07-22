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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kz.snapar.app.R
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparGold
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun RoutesScreen(
    modifier: Modifier,
    language: AppLanguage,
    routes: List<TravelRoute>,
    onRoute: (TravelRoute) -> Unit,
    onBuild: () -> Unit,
) {
    val labels = strings(language)
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("all") }
    val filters = listOf(
        "all" to localRoute(language, "Барлығы", "Все", "All"),
        "budget" to localRoute(language, "Баға", "Цена", "Price"),
        "trust" to "Trust",
        "rating" to localRoute(language, "Рейтинг", "Рейтинг", "Rating"),
        "near" to localRoute(language, "Қашықтық", "Расстояние", "Distance"),
        "360" to "360°",
    )
    val visibleRoutes = routes
        .filter { query.isBlank() || it.title.value(language).contains(query, true) }
        .let {
            when (filter) {
                "budget" -> it.sortedBy(TravelRoute::price)
                "trust" -> it.sortedByDescending(TravelRoute::trust)
                "rating" -> it.sortedByDescending(TravelRoute::rating)
                "near" -> it.sortedBy(TravelRoute::distanceKm)
                "360" -> it.filter { route -> route.id == 101 || route.interests.contains("photo") }
                else -> it
            }
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp),
    ) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(330.dp),
            ) {
                AsyncImage(R.drawable.routes_map, "Kazakhstan map", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x33000000), Color(0x11FFFFFF), Color(0xFFF4FBF9)))))
                Column(
                    Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(localRoute(language, "Қайда барасыз?", "Куда отправимся?", "Where to?")) },
                        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = SnaparPrimary) },
                        trailingIcon = { Icon(Icons.Outlined.FilterList, null, tint = SnaparPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(30.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(.95f),
                            unfocusedContainerColor = Color.White.copy(.95f),
                            focusedBorderColor = SnaparTurquoise,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { item ->
                            FilterChip(
                                selected = filter == item.first,
                                onClick = { filter = item.first },
                                label = { Text(item.second) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(.9f),
                                    selectedContainerColor = SnaparPrimary,
                                    selectedLabelColor = Color.White,
                                ),
                            )
                        }
                    }
                }
                MapMarker(
                    "Көлсай",
                    Modifier.align(Alignment.Center).padding(top = 30.dp),
                    onClick = { routes.firstOrNull()?.let(onRoute) },
                )
                MapMarker(
                    "Шарын",
                    Modifier.align(Alignment.BottomEnd).padding(end = 58.dp, bottom = 38.dp),
                    SnaparGold,
                    onClick = { routes.firstOrNull { it.id == 102 }?.let(onRoute) },
                )
                Button(
                    onClick = onBuild,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null)
                    Spacer(Modifier.width(7.dp))
                    Text(labels.routeBuilder)
                }
            }
        }

        item {
            Column(Modifier.padding(horizontal = 18.dp)) {
                SectionTitle(localRoute(language, "Танымал бағыттар", "Популярные маршруты", "Popular routes"))
            }
        }

        items(visibleRoutes, key = { it.id }) { route ->
            RouteCard(route, language, Modifier.padding(horizontal = 18.dp)) { onRoute(route) }
        }

        if (visibleRoutes.isEmpty()) {
            item {
                Card(
                    Modifier.padding(horizontal = 18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        localRoute(language, "Маршрут табылмады", "Маршруты не найдены", "No routes found"),
                        Modifier.padding(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MapMarker(
    label: String,
    modifier: Modifier,
    color: Color = SnaparPrimary,
    onClick: () -> Unit = {},
) {
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(54.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Landscape, null, tint = Color.White)
        }
        Surface(shape = RoundedCornerShape(5.dp), color = Color.White) {
            Text(label, Modifier.padding(horizontal = 7.dp, vertical = 2.dp), fontSize = 11.sp)
        }
    }
}

@Composable
private fun RouteCard(
    route: TravelRoute,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(185.dp),
        ) {
            AsyncImage(route.image, route.title.value(language), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xC4001825)))))
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                color = Color.White.copy(.9f),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    "${route.trust}% Trust",
                    Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                route.title.value(language),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(15.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .background(SnaparPrimary, CircleShape),
            ) {
                Icon(Icons.Outlined.ArrowForward, null, tint = Color.White)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${route.days} ${localRoute(language, "күн", "дн.", "days")}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, null, tint = SnaparGold, modifier = Modifier.size(15.dp))
                    Text(route.rating.toString(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Text("${route.distanceKm} км", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(formatTenge(route.price), color = SnaparPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun localRoute(language: AppLanguage, kk: String, ru: String, en: String): String = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

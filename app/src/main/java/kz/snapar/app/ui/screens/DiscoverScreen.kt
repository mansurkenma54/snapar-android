package kz.snapar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
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
import androidx.compose.material3.OutlinedButton
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
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.Place
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
fun DiscoverScreen(
    modifier: Modifier,
    state: SnaparState,
    onPlace: (Place) -> Unit,
    onRoute: (Place) -> Unit,
    onSai: (Place) -> Unit,
    onGeoSnap: () -> Unit,
    onShorts: (CommunityPost) -> Unit,
) {
    val language = state.language
    val labels = strings(language)
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    val categories = listOf(
        "all" to local(language, "Барлығы", "Все", "All"),
        "mountain" to local(language, "Таулар", "Горы", "Mountains"),
        "lake" to local(language, "Көлдер", "Озёра", "Lakes"),
        "photo" to local(language, "Фото", "Фото", "Photo"),
        "hidden" to local(language, "Жасырын", "Скрытое", "Hidden"),
    )
    val filtered = SampleData.places
        .filterNot { it.id in state.dislikedIds }
        .filter { place ->
            val text = "${place.name.value(language)} ${place.region.value(language)}"
            val searchMatches = query.isBlank() || text.contains(query, ignoreCase = true)
            val categoryMatches = when (selectedCategory) {
                "mountain" -> place.tags.any { it.en.contains("Mountain", true) }
                "lake" -> place.tags.any { it.en.contains("Lake", true) }
                "photo" -> place.tags.any { it.en.contains("Photo", true) }
                "hidden" -> place.id >= 5
                else -> true
            }
            searchMatches && categoryMatches
        }
        .sortedByDescending { if (it.id in state.likedIds) 1 else 0 }
    val posts = state.sessionPosts + SampleData.posts

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("${labels.whereToday} ✨", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(labels.searchHint, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = SnaparTurquoise) },
                    trailingIcon = {
                        IconButton(onClick = onGeoSnap) {
                            Icon(Icons.Outlined.CameraAlt, "GeoSnap", tint = SnaparPrimary)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEEF5F4),
                        unfocusedContainerColor = Color(0xFFEEF5F4),
                        focusedBorderColor = SnaparTurquoise,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { (key, title) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(title) },
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(labels.nearby, labels.seeAll)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(filtered.take(4)) { place ->
                        NearbyCard(place, language) { onPlace(place) }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Text(
                        local(language, "Сұраныс бойынша орын табылмады.", "Места не найдены.", "No places found."),
                        modifier = Modifier.padding(20.dp),
                    )
                }
            }
        } else {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("${labels.trending} 🔥")
                    FeaturedCard(
                        place = filtered.first(),
                        language = language,
                        liked = filtered.first().id in state.likedIds,
                        saved = filtered.first().id in state.savedIds,
                        onPlace = { onPlace(filtered.first()) },
                        onLike = { state.toggleLike(filtered.first().id) },
                        onDislike = { state.dislike(filtered.first().id) },
                        onSave = { state.toggleSaved(filtered.first().id) },
                        onRoute = { onRoute(filtered.first()) },
                        onSai = { onSai(filtered.first()) },
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle("Hidden Kazakhstan")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(SampleData.places.filter { it.id >= 5 }) { place ->
                        HiddenCard(place, language) { onPlace(place) }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle(labels.community, labels.seeAll) {
                    posts.firstOrNull()?.let(onShorts)
                }
                posts.chunked(2).take(2).forEach { rowPosts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowPosts.forEach { post ->
                            CommunityThumb(post, Modifier.weight(1f)) { onShorts(post) }
                        }
                        if (rowPosts.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyCard(place: Place, language: AppLanguage, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Box {
            AsyncImage(
                model = place.image,
                contentDescription = place.name.value(language),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(134.dp),
                contentScale = ContentScale.Crop,
            )
            RatingBadge(
                place.rating,
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )
        }
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                place.name.value(language),
                fontWeight = FontWeight.SemiBold,
                color = SnaparNavy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Meta(Icons.Outlined.DirectionsCar, "${place.distanceKm} км")
                Meta(Icons.Outlined.Cloud, "${place.weather.temperature}°C")
            }
        }
    }
}

@Composable
private fun FeaturedCard(
    place: Place,
    language: AppLanguage,
    liked: Boolean,
    saved: Boolean,
    onPlace: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onSave: () -> Unit,
    onRoute: () -> Unit,
    onSai: () -> Unit,
) {
    val labels = strings(language)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            onClick = onPlace,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.82f),
            shape = RoundedCornerShape(18.dp),
        ) {
            Box(Modifier.fillMaxSize()) {
                AsyncImage(place.image, place.name.value(language), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color(0xD9001720)))),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    GlassBadge(Icons.Outlined.Cloud, "${place.weather.temperature}°C")
                    GlassBadge(Icons.Rounded.Star, "${place.trustScore}%")
                }
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp),
                ) {
                    Text(place.name.value(language), style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(place.region.value(language), color = Color.White, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Text(formatTenge(place.averageCost), color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SnaparTurquoise.copy(alpha = 0.10f),
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.HelpOutline, null, tint = SnaparPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Column {
                    Text(labels.whyRecommended, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text(place.recommendation.value(language), color = SnaparMuted, fontSize = 12.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            OutlinedButton(onClick = onDislike, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Outlined.Close, null)
                Spacer(Modifier.width(4.dp))
                Text(labels.notInteresting, maxLines = 1, fontSize = 11.sp)
            }
            Button(
                onClick = onLike,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
            ) {
                Icon(if (liked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder, null)
                Spacer(Modifier.width(4.dp))
                Text(labels.interesting, maxLines = 1, fontSize = 11.sp)
            }
            ActionIcon(if (saved) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder, onSave)
            ActionIcon(Icons.Outlined.Map, onRoute)
            ActionIcon(Icons.Rounded.Star, onSai, SnaparGold)
        }
    }
}

@Composable
private fun HiddenCard(place: Place, language: AppLanguage, onClick: () -> Unit) {
    Box(
        Modifier
            .width(180.dp)
            .height(190.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(place.image, place.name.value(language), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xB0000000)))))
        Text(
            place.name.value(language),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        )
    }
}

@Composable
private fun CommunityThumb(post: CommunityPost, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .height(156.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(post.image, post.user, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x99000000)))))
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("@${post.user}", color = Color.White, fontSize = 10.sp)
            Text("♡ ${compactNumber(post.likes)}", color = Color.White, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, tint: Color = SnaparNavy) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8EFEE)),
    ) {
        Icon(icon, null, tint = tint)
    }
}

@Composable
private fun RatingBadge(rating: Double, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Star, null, tint = SnaparGold, modifier = Modifier.size(14.dp))
        Text(rating.toString(), color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun GlassBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.86f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, tint = SnaparPrimary, modifier = Modifier.size(16.dp))
        Text(text, color = SnaparNavy, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun Meta(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = SnaparMuted, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = SnaparMuted, fontSize = 12.sp)
    }
}

private fun local(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

private fun compactNumber(value: Int): String =
    if (value >= 1000) "${value / 1000}.${(value % 1000) / 100}k" else value.toString()

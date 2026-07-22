package kz.snapar.app.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.widget.VideoView
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
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import kz.snapar.app.R
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.LocalText
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun GeoSnapScreen(
    state: SnaparState,
    onBack: () -> Unit,
    onOpenShorts: (CommunityPost) -> Unit,
) {
    val context = LocalContext.current
    val labels = strings(state.language)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedMedia by remember { mutableStateOf<Uri?>(null) }
    var mediaType by remember { mutableStateOf("image") }
    var caption by remember { mutableStateOf("") }
    var is360 by remember { mutableStateOf(false) }
    var gpsEnabled by remember { mutableStateOf(false) }
    var locationText by remember { mutableStateOf(geoLocal(state.language, "GPS өшірулі", "GPS выключен", "GPS off")) }
    var commentsPost by remember { mutableStateOf<CommunityPost?>(null) }
    var commentText by remember { mutableStateOf("") }

    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            selectedMedia = uri
            mediaType = if (context.contentResolver.getType(uri)?.startsWith("video") == true) "video" else "image"
        }
    }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) {
            locationText = lastLocation(context) ?: geoLocal(state.language, "Алматы · GPS расталды", "Алматы · GPS подтверждён", "Almaty · GPS verified")
        } else {
            gpsEnabled = false
            locationText = geoLocal(state.language, "GPS рұқсаты жоқ", "Нет доступа к GPS", "GPS permission denied")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") }
                Text("GeoSnap", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        },
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(
                        Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(13.dp),
                    ) {
                        if (selectedMedia == null) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(185.dp)
                                    .background(SnaparTurquoise.copy(.1f), RoundedCornerShape(16.dp))
                                    .clickable { mediaPicker.launch(arrayOf("image/*", "video/*")) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.AddAPhoto, null, tint = SnaparTurquoise, modifier = Modifier.size(48.dp))
                                    Text(
                                        geoLocal(state.language, "Саяхат сәтін қосыңыз", "Добавьте момент путешествия", "Add a travel moment"),
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        } else {
                            if (mediaType == "video") {
                                AndroidView(
                                    modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp)),
                                    factory = { videoContext ->
                                        VideoView(videoContext).apply {
                                            setVideoURI(selectedMedia)
                                            setOnPreparedListener { player ->
                                                player.isLooping = true
                                                start()
                                            }
                                        }
                                    },
                                )
                            } else {
                                AsyncImage(
                                    selectedMedia,
                                    "Selected photo",
                                    Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                        Button(
                            onClick = { mediaPicker.launch(arrayOf("image/*", "video/*")) },
                            colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Icon(Icons.Outlined.PhotoLibrary, null)
                            Spacer(Modifier.width(7.dp))
                            Text(labels.choosePhoto)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = mediaType == "image" && !is360,
                                onClick = {
                                    mediaType = "image"
                                    is360 = false
                                    mediaPicker.launch(arrayOf("image/*"))
                                },
                                label = { Text("Фото") },
                                colors = snapChipColors(),
                            )
                            FilterChip(
                                selected = mediaType == "image" && is360,
                                onClick = {
                                    mediaType = "image"
                                    is360 = true
                                    mediaPicker.launch(arrayOf("image/*"))
                                },
                                label = { Text("360°") },
                                colors = snapChipColors(),
                            )
                            FilterChip(
                                selected = mediaType == "video",
                                onClick = {
                                    mediaType = "video"
                                    is360 = false
                                    mediaPicker.launch(arrayOf("video/*"))
                                },
                                label = { Text("Видео") },
                                colors = snapChipColors(),
                            )
                        }
                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(labels.caption) },
                            minLines = 2,
                            shape = RoundedCornerShape(14.dp),
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Outlined.LocationOn, null, tint = SnaparPrimary)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(geoLocal(state.language, "GPS орнын тіркеу", "Прикрепить GPS", "Attach GPS"), fontWeight = FontWeight.SemiBold)
                                Text(locationText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = gpsEnabled,
                                onCheckedChange = { enabled ->
                                    gpsEnabled = enabled
                                    if (enabled) {
                                        locationPermission.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                            ),
                                        )
                                    }
                                },
                            )
                        }
                        Button(
                            onClick = {
                                val image = selectedMedia ?: R.drawable.charyn
                                val post = CommunityPost(
                                    id = "user-${System.currentTimeMillis()}",
                                    user = "Азамат Н.",
                                    location = LocalText(locationText, locationText, locationText),
                                    caption = LocalText(
                                        caption.ifBlank { "Жаңа саяхат сәті" },
                                        caption.ifBlank { "Новый момент путешествия" },
                                        caption.ifBlank { "A new travel moment" },
                                    ),
                                    image = image,
                                    likes = 0,
                                    comments = emptyList(),
                                    is360 = is360,
                                    verifiedGps = gpsEnabled,
                                    mediaType = mediaType,
                                )
                                state.publish(post)
                                selectedMedia = null
                                caption = ""
                                scope.launch { snackbar.showSnackbar(geoLocal(state.language, "GeoSnap жарияланды +75 XP", "GeoSnap опубликован +75 XP", "GeoSnap published +75 XP")) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedMedia != null,
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Icon(Icons.Rounded.CheckCircle, null)
                            Spacer(Modifier.width(7.dp))
                            Text(labels.publish)
                        }
                    }
                }
            }

            item {
                SectionTitle(geoLocal(state.language, "Соңғы GeoSnap", "Последние GeoSnap", "Latest GeoSnaps"))
            }
            items(state.sessionPosts + kz.snapar.app.data.SampleData.posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenShorts(post) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(38.dp).background(SnaparTurquoise, CircleShape), contentAlignment = Alignment.Center) {
                                Text(post.user.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(9.dp))
                            Column(Modifier.weight(1f)) {
                                Text(post.user, fontWeight = FontWeight.Bold)
                                Text(post.location.value(state.language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (post.verifiedGps) Icon(Icons.Outlined.Public, "GPS", tint = SnaparPrimary)
                        }
                        if (post.mediaType == "video") {
                            AndroidView(
                                modifier = Modifier.fillMaxWidth().height(260.dp),
                                factory = { videoContext ->
                                    VideoView(videoContext).apply {
                                        setVideoURI(Uri.parse(post.image.toString()))
                                        setOnPreparedListener { player ->
                                            player.isLooping = true
                                            start()
                                        }
                                    }
                                },
                            )
                        } else {
                            AsyncImage(post.image, post.caption.value(state.language), Modifier.fillMaxWidth().height(260.dp), contentScale = ContentScale.Crop)
                        }
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(
                                modifier = Modifier.clickable { state.togglePostLike(post.id) },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    if (post.id in state.likedPostIds) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                                    null,
                                    tint = if (post.id in state.likedPostIds) Color.Red else SnaparNavy,
                                )
                                Text(" ${post.likes + if (post.id in state.likedPostIds) 1 else 0}")
                            }
                            Row(
                                modifier = Modifier.clickable { commentsPost = post },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Outlined.ChatBubbleOutline, null)
                                Text(" ${state.commentsFor(post).size}")
                            }
                            Icon(
                                Icons.Outlined.Share,
                                "Share",
                                modifier = Modifier.clickable {
                                    context.startActivity(
                                        Intent.createChooser(
                                            Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, post.caption.value(state.language))
                                            },
                                            "Snapar",
                                        ),
                                    )
                                },
                            )
                            if (post.is360) Text("360°", color = SnaparPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    commentsPost?.let { post ->
        AlertDialog(
            onDismissRequest = { commentsPost = null },
            title = { Text(geoLocal(state.language, "Пікірлер", "Комментарии", "Comments")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val comments = state.commentsFor(post)
                    if (comments.isEmpty()) Text(geoLocal(state.language, "Әзірге пікір жоқ", "Комментариев пока нет", "No comments yet"))
                    comments.forEach { Text("• $it") }
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text(geoLocal(state.language, "Пікір", "Комментарий", "Comment")) },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.addComment(post.id, commentText)
                        commentText = ""
                    },
                    enabled = commentText.isNotBlank(),
                ) { Text(geoLocal(state.language, "Жіберу", "Отправить", "Send")) }
            },
            dismissButton = {
                TextButton(onClick = {
                    commentsPost = null
                    commentText = ""
                }) { Text(geoLocal(state.language, "Жабу", "Закрыть", "Close")) }
            },
        )
    }
}

@Composable
private fun snapChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = SnaparTurquoise,
    selectedLabelColor = Color.White,
)

@SuppressLint("MissingPermission")
private fun lastLocation(context: Context): String? {
    val fine = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!fine && !coarse) return null
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val location = manager.getProviders(true)
        .mapNotNull { provider -> runCatching { manager.getLastKnownLocation(provider) }.getOrNull() }
        .maxByOrNull { it.time }
        ?: return null
    return "%.4f, %.4f · GPS".format(location.latitude, location.longitude)
}

private fun geoLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

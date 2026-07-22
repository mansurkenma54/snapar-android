package kz.snapar.app.ui.screens

import android.content.Intent
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.pressScale
import kz.snapar.app.ui.theme.SnaparTurquoise
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ShortsScreen(
    initialPost: CommunityPost,
    posts: List<CommunityPost>,
    language: AppLanguage,
    state: SnaparState,
    onBack: () -> Unit,
    onPlace: () -> Unit,
    onSai: () -> Unit,
) {
    val ordered = remember(posts, initialPost) {
        listOf(initialPost) + posts.filterNot { it.id == initialPost.id }
    }
    val pager = rememberPagerState(pageCount = { ordered.size })
    val context = LocalContext.current
    var commentPost by remember { mutableStateOf<CommunityPost?>(null) }
    var commentText by remember { mutableStateOf("") }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        VerticalPager(state = pager, modifier = Modifier.fillMaxSize()) { index ->
            val post = ordered[index]
            Box(Modifier.fillMaxSize()) {
                PostMedia(post, Modifier.fillMaxSize())
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color(0x33000000), Color.Transparent, Color(0xCC000000)))),
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .navigationBarsPadding()
                        .padding(start = 18.dp, end = 90.dp, bottom = 32.dp),
                ) {
                    Text("@${post.user}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(post.location.value(language), color = Color.White.copy(alpha = 0.78f))
                    Spacer(Modifier.size(8.dp))
                    Text(post.caption.value(language), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    if (post.is360) {
                        Text(
                            "360°",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                        )
                    }
                }
                Column(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    ShortsAction(
                        if (post.id in state.likedPostIds) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                        "${post.likes + if (post.id in state.likedPostIds) 1 else 0}",
                    ) { state.togglePostLike(post.id) }
                    ShortsAction(Icons.Outlined.ChatBubbleOutline, "${state.commentsFor(post).size}") {
                        commentPost = post
                    }
                    ShortsAction(Icons.Outlined.Map, "Route", onPlace)
                    ShortsAction(Icons.Outlined.AutoAwesome, "SAI", onSai)
                    ShortsAction(Icons.Outlined.Share, "") {
                        context.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${post.caption.value(language)}\nSnapar")
                                },
                                "Snapar",
                            ),
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(10.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape),
        ) {
            Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
        }
    }

    commentPost?.let { post ->
        AlertDialog(
            onDismissRequest = {
                commentPost = null
                commentText = ""
            },
            title = { Text(shortsLocal(language, "Пікірлер", "Комментарии", "Comments")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.commentsFor(post).forEach { comment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Text(
                                comment,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text(shortsLocal(language, "Пікір жазыңыз", "Напишите комментарий", "Write a comment")) },
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
                ) { Text(shortsLocal(language, "Жіберу", "Отправить", "Send")) }
            },
            dismissButton = {
                TextButton(onClick = { commentPost = null }) { Text(shortsLocal(language, "Жабу", "Закрыть", "Close")) }
            },
        )
    }
}

@Composable
private fun PostMedia(post: CommunityPost, modifier: Modifier) {
    if (post.mediaType == "video") {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                VideoView(context).apply {
                    setVideoURI(android.net.Uri.parse(post.image.toString()))
                    setOnPreparedListener { player ->
                        player.isLooping = true
                        start()
                    }
                }
            },
        )
    } else {
        var panX by remember(post.id) { mutableStateOf(0f) }
        AsyncImage(
            post.image,
            post.user,
            modifier
                .graphicsLayer {
                    if (post.is360) {
                        scaleX = 1.35f
                        scaleY = 1.35f
                        translationX = panX
                    }
                }
                .then(
                    if (post.is360) {
                        Modifier.pointerInput(post.id) {
                            detectDragGestures { change, drag ->
                                change.consume()
                                panX = (panX + drag.x).coerceIn(-420f, 420f)
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ShortsAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                .pressScale(),
        ) {
            Icon(icon, label, tint = if (label == "SAI") SnaparTurquoise else Color.White)
        }
        if (label.isNotBlank()) Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

private fun shortsLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

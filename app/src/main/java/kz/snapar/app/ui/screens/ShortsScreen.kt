package kz.snapar.app.ui.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun ShortsScreen(
    initialPost: CommunityPost,
    posts: List<CommunityPost>,
    language: AppLanguage,
    onBack: () -> Unit,
    onPlace: () -> Unit,
    onSai: () -> Unit,
) {
    val ordered = remember(posts, initialPost) {
        listOf(initialPost) + posts.filterNot { it.id == initialPost.id }
    }
    val pager = rememberPagerState(pageCount = { ordered.size })
    val liked = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        VerticalPager(state = pager, modifier = Modifier.fillMaxSize()) { index ->
            val post = ordered[index]
            Box(Modifier.fillMaxSize()) {
                AsyncImage(post.image, post.caption.value(language), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
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
                        if (liked[post.id] == true) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                        "${post.likes + if (liked[post.id] == true) 1 else 0}",
                    ) { liked[post.id] = liked[post.id] != true }
                    ShortsAction(Icons.Outlined.ChatBubbleOutline, "${post.comments.size}") {}
                    ShortsAction(Icons.Outlined.Map, "Route", onPlace)
                    ShortsAction(Icons.Outlined.AutoAwesome, "SAI", onSai)
                    ShortsAction(Icons.Outlined.Share, "") {}
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
            modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape),
        ) {
            Icon(icon, label, tint = if (label == "SAI") SnaparTurquoise else Color.White)
        }
        if (label.isNotBlank()) Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

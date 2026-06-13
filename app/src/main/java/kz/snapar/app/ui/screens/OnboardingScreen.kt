package kz.snapar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparSurface
import kz.snapar.app.ui.theme.SnaparTurquoise
import kotlinx.coroutines.launch

private data class IntroPage(
    val icon: ImageVector,
    val title: String,
    val text: String,
)

@Composable
fun OnboardingScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onFinish: () -> Unit,
) {
    val pages = when (language) {
        AppLanguage.Kazakh -> listOf(
            IntroPage(Icons.Outlined.AutoAwesome, "Қазақстанды жаңаша ашыңыз", "Шынайы фотолар, жасырын орындар және талғамыңызға сай ұсыныстар."),
            IntroPage(Icons.Outlined.Map, "Маршрут бірнеше секундта", "Бюджет, уақыт және қызығушылыққа қарай SAI толық сапар жоспарын құрады."),
            IntroPage(Icons.Outlined.CameraAlt, "Саяхат қауымдастығы", "GeoSnap арқылы фото, 360° көрініс бөлісіп, Passport XP жинаңыз."),
        )
        AppLanguage.Russian -> listOf(
            IntroPage(Icons.Outlined.AutoAwesome, "Откройте Казахстан заново", "Живые фотографии, скрытые места и персональные рекомендации."),
            IntroPage(Icons.Outlined.Map, "Маршрут за секунды", "SAI создаст план поездки по бюджету, времени и интересам."),
            IntroPage(Icons.Outlined.CameraAlt, "Сообщество путешественников", "Делитесь фото и 360° видами в GeoSnap и получайте XP."),
        )
        AppLanguage.English -> listOf(
            IntroPage(Icons.Outlined.AutoAwesome, "Rediscover Kazakhstan", "Authentic photos, hidden gems and recommendations tailored to you."),
            IntroPage(Icons.Outlined.Map, "Routes in seconds", "SAI builds a complete trip around your budget, time and interests."),
            IntroPage(Icons.Outlined.CameraAlt, "Travel community", "Share photos and 360° views in GeoSnap and earn Passport XP."),
        )
    }
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SnaparSurface, Color(0xFFD8F7F4), SnaparSurface),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Snapar", color = SnaparTurquoise, fontWeight = FontWeight.Bold, fontSize = 34.sp)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AppLanguage.entries.forEach { item ->
                    FilterChip(
                        selected = item == language,
                        onClick = { onLanguageChange(item) },
                        label = { Text(item.code.uppercase()) },
                    )
                }
            }
            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(124.dp)
                            .background(SnaparTurquoise.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(item.icon, null, tint = SnaparPrimary, modifier = Modifier.size(62.dp))
                    }
                    Spacer(Modifier.height(32.dp))
                    Text(
                        item.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = SnaparNavy,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        item.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF3C4948),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pager.currentPage == index) 18.dp else 8.dp, 8.dp)
                            .background(
                                if (pager.currentPage == index) SnaparTurquoise else Color(0xFFBBCAC8),
                                CircleShape,
                            ),
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = {
                    if (pager.currentPage == pages.lastIndex) onFinish()
                    else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
            ) {
                val label = when (language) {
                    AppLanguage.Kazakh -> if (pager.currentPage == pages.lastIndex) "Бастау" else "Келесі"
                    AppLanguage.Russian -> if (pager.currentPage == pages.lastIndex) "Начать" else "Далее"
                    AppLanguage.English -> if (pager.currentPage == pages.lastIndex) "Start" else "Next"
                }
                Text(label, modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

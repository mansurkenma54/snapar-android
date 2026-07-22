package kz.snapar.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    onFinish: (String, String) -> Unit,
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
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    var showRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF0FAFA), Color(0xFFD4F4F0), Color(0xFFF0FAFA)),
                ),
            ),
    ) {
        // Decorative circles background
        Box(
            Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = 80f; translationY = -60f }
                .background(
                    Brush.radialGradient(listOf(SnaparTurquoise.copy(.08f), Color.Transparent)),
                    CircleShape,
                ),
        )
        Box(
            Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { translationX = -60f; translationY = 60f }
                .background(
                    Brush.radialGradient(listOf(SnaparPrimary.copy(.06f), Color.Transparent)),
                    CircleShape,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                visible = entered,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 3 },
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "✦ Snapar",
                        color = SnaparTurquoise,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        letterSpacing = (-0.5).sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when (language) {
                            AppLanguage.Kazakh -> "Қазақстанды жаңаша ашыңыз"
                            AppLanguage.Russian -> "Открывайте Казахстан по-новому"
                            AppLanguage.English -> "Rediscover Kazakhstan"
                        },
                        color = SnaparPrimary.copy(.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { item ->
                    FilterChip(
                        selected = item == language,
                        onClick = { onLanguageChange(item) },
                        label = { Text(item.code.uppercase(), fontWeight = FontWeight.Bold) },
                    )
                }
            }
            Crossfade(
                targetState = showRegister,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "onboarding-step",
            ) { registering ->
                if (registering) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(modifier = Modifier.size(104.dp), contentAlignment = Alignment.Center) {
                            Box(
                                Modifier
                                    .size(104.dp)
                                    .background(
                                        Brush.radialGradient(listOf(SnaparTurquoise.copy(.14f), Color.Transparent)),
                                        CircleShape,
                                    ),
                            )
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(
                                        Brush.linearGradient(listOf(SnaparTurquoise.copy(.22f), SnaparPrimary.copy(.12f))),
                                        CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Outlined.Person, null, tint = SnaparPrimary, modifier = Modifier.size(38.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            when (language) {
                                AppLanguage.Kazakh -> "Танысайық"
                                AppLanguage.Russian -> "Давайте познакомимся"
                                AppLanguage.English -> "Let's get acquainted"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = SnaparNavy,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when (language) {
                                AppLanguage.Kazakh -> "Атыңызды енгізіңіз, ол профиліңізде көрсетіледі"
                                AppLanguage.Russian -> "Введите имя — оно появится в вашем профиле"
                                AppLanguage.English -> "Enter your name — it'll show on your profile"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF3C5450),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(24.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    when (language) {
                                        AppLanguage.Kazakh -> "Атыңыз"
                                        AppLanguage.Russian -> "Имя"
                                        AppLanguage.English -> "Name"
                                    },
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnaparTurquoise),
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    when (language) {
                                        AppLanguage.Kazakh -> "Email (міндетті емес)"
                                        AppLanguage.Russian -> "Email (необязательно)"
                                        AppLanguage.English -> "Email (optional)"
                                    },
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnaparTurquoise),
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        HorizontalPager(
                            state = pager,
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) { page ->
                            val item = pages[page]
                            val isActive = pager.currentPage == page
                            val iconScale by animateFloatAsState(
                                targetValue = if (isActive) 1f else 0.88f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                                label = "icon-scale",
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Box(
                                    modifier = Modifier.size(144.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    // Outer glow
                                    Box(
                                        Modifier
                                            .size(144.dp)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(SnaparTurquoise.copy(.14f), Color.Transparent),
                                                ),
                                                CircleShape,
                                            ),
                                    )
                                    // Inner circle
                                    Box(
                                        modifier = Modifier
                                            .scale(iconScale)
                                            .size(104.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(SnaparTurquoise.copy(.22f), SnaparPrimary.copy(.12f)),
                                                ),
                                                CircleShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(item.icon, null, tint = SnaparPrimary, modifier = Modifier.size(52.dp))
                                    }
                                }
                                Spacer(Modifier.height(32.dp))
                                Text(
                                    item.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = SnaparNavy,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    item.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF3C5450),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp,
                                )
                            }
                        }

                        // Dot indicator — анимациялы
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            pages.indices.forEach { index ->
                                val active = pager.currentPage == index
                                val dotWidth by animateDpAsState(
                                    targetValue = if (active) 24.dp else 8.dp,
                                    animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                                    label = "dot-w",
                                )
                                Box(
                                    modifier = Modifier
                                        .size(dotWidth, 8.dp)
                                        .background(
                                            if (active) SnaparTurquoise else Color(0xFFB0C8C5),
                                            CircleShape,
                                        ),
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    when {
                        showRegister -> onFinish(name, email)
                        pager.currentPage == pages.lastIndex -> showRegister = true
                        else -> scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    }
                },
                enabled = !showRegister || name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnaparTurquoise,
                ),
            ) {
                val label = when (language) {
                    AppLanguage.Kazakh -> if (showRegister) "Аяқтау ✓" else if (pager.currentPage == pages.lastIndex) "Бастау 🚀" else "Келесі →"
                    AppLanguage.Russian -> if (showRegister) "Готово ✓" else if (pager.currentPage == pages.lastIndex) "Начать 🚀" else "Далее →"
                    AppLanguage.English -> if (showRegister) "Finish ✓" else if (pager.currentPage == pages.lastIndex) "Start 🚀" else "Next →"
                }
                Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

package kz.snapar.app.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.snapar.app.data.SaiRepository
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.ChatMessage
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.components.pressScale
import kz.snapar.app.ui.theme.SnaparDark
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise
import kotlinx.coroutines.launch

@Composable
fun SaiScreen(
    language: AppLanguage,
    messages: SnapshotStateList<ChatMessage>,
    onBack: () -> Unit,
    onRouteGenerated: (TravelRoute) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val repository = remember { SaiRepository() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    text = saiLocal(
                        language,
                        "Сәлем! Мен SAI — Қазақстан бойынша саяхат көмекшісімін. Қай бағытты жоспарлаймыз?",
                        "Привет! Я SAI — помощник по путешествиям по Казахстану. Куда спланируем поездку?",
                        "Hi! I am SAI, your Kazakhstan travel assistant. Where shall we plan a trip?",
                    ),
                    isUser = false,
                ),
            )
        }
    }
    val suggestions = listOf(
        saiLocal(language, "3 күн табиғат", "3 дня на природе", "3 days in nature"),
        saiLocal(language, "Отбасылық тур", "Семейный тур", "Family trip"),
        saiLocal(language, "60 000 ₸ бюджет", "Бюджет 60 000 ₸", "60,000 ₸ budget"),
        saiLocal(language, "Жаяу серуен", "Пеший маршрут", "Hiking trip"),
    )

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || isThinking) return
        messages += ChatMessage(clean, true)
        input = ""
        isThinking = true
        scope.launch {
            val reply = repository.answer(clean, language)
            messages += ChatMessage(reply.text, false, reply.route)
            isThinking = false
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val heard = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!heard.isNullOrBlank()) {
                input = heard
                send(heard)
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SnaparDark)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        // SAI header with gradient background
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0D2235), SnaparDark),
                    ),
                )
                .padding(horizontal = 6.dp, vertical = 10.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
                }
                val infinite = rememberInfiniteTransition(label = "sai-hdr")
                val glowAlpha by infinite.animateFloat(
                    initialValue = 0.25f, targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "hdr-glow",
                )
                Box(
                    Modifier.size(50.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(50.dp)
                            .background(
                                Brush.radialGradient(listOf(SnaparTurquoise.copy(glowAlpha), Color.Transparent)),
                                CircleShape,
                            ),
                    )
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(
                                Brush.linearGradient(listOf(SnaparTurquoise, SnaparPrimary)),
                                CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("SAI Assistant", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(6.dp).background(Color(0xFF4BDB93), CircleShape))
                        Text("online · ${language.label}", color = Color(0xFF9EB0C1), fontSize = 12.sp)
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(messages) { message ->
                MessageBubble(message, language) {
                    message.route?.let(onRouteGenerated)
                }
            }
            if (isThinking) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .background(SnaparTurquoise.copy(.2f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, null, tint = SnaparTurquoise, modifier = Modifier.size(16.dp))
                        }
                        Surface(
                            color = Color(0xFF22384D),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                        ) {
                            TypingDots()
                        }
                    }
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { suggestion ->
                Surface(
                    modifier = Modifier.pressScale(0.94f).clickable { send(suggestion) },
                    color = Color(0xFF1E3448),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SnaparTurquoise.copy(.25f)),
                ) {
                    Text(
                        suggestion,
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color(0xFFD0E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(saiLocal(language, "Сұрақ қойыңыз...", "Задайте вопрос...", "Ask a question...")) },
                trailingIcon = {
                    IconButton(onClick = { send(input) }) {
                        Icon(Icons.Outlined.Send, null, tint = SnaparTurquoise)
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF223549),
                    unfocusedContainerColor = Color(0xFF223549),
                    focusedBorderColor = SnaparTurquoise,
                    unfocusedBorderColor = Color(0xFF516274),
                    focusedPlaceholderColor = Color(0xFF9EB0C1),
                    unfocusedPlaceholderColor = Color(0xFF9EB0C1),
                ),
                maxLines = 3,
            )
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.code)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "SAI")
                    }
                    runCatching { voiceLauncher.launch(intent) }
                        .onFailure {
                            Toast.makeText(
                                context,
                                saiLocal(language, "Дауыс қызметі табылмады", "Голосовой сервис недоступен", "Voice service is unavailable"),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                },
                modifier = Modifier
                    .size(54.dp)
                    .background(SnaparTurquoise, CircleShape),
            ) {
                Icon(Icons.Outlined.Mic, "Voice", tint = Color.White)
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val infinite = rememberInfiniteTransition(label = "dots")
    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { idx ->
            val offsetY by infinite.animateFloat(
                initialValue = 0f, targetValue = -6f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = 900
                        0f at 0 using LinearEasing
                        -6f at 200 using LinearEasing
                        0f at 400 using LinearEasing
                        0f at 900 using LinearEasing
                    },
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = androidx.compose.animation.core.StartOffset(idx * 120),
                ),
                label = "dot$idx",
            )
            Box(
                Modifier
                    .size(7.dp)
                    .offset(y = offsetY.dp)
                    .background(SnaparTurquoise.copy(.75f), CircleShape),
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, language: AppLanguage, onRoute: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { if (message.isUser) it / 2 else -it / 2 },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        ) {
            if (!message.isUser) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(listOf(SnaparTurquoise, SnaparPrimary)),
                            CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(17.dp))
                }
                Spacer(Modifier.width(8.dp))
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth(.84f)
                    .heightIn(min = 44.dp),
                color = if (message.isUser) SnaparTurquoise else Color(0xFF1E3448),
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (message.isUser) 18.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 18.dp,
                ),
            ) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(message.text, color = Color.White, style = MaterialTheme.typography.bodyLarge, lineHeight = 22.sp)
                    message.route?.let { route ->
                        Surface(
                            color = Color(0xFF0F1E2E),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SnaparTurquoise.copy(.2f)),
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(route.title.value(language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    RouteChip("${route.days} ${saiLocal(language, "күн", "дн.", "days")}")
                                    RouteChip("${route.distanceKm} км")
                                    RouteChip(formatTenge(route.price))
                                }
                                Button(
                                    onClick = onRoute,
                                    colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Outlined.Map, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(saiLocal(language, "Толық жоспар", "Полный план", "Full plan"), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteChip(text: String) {
    Surface(color = SnaparTurquoise.copy(.15f), shape = RoundedCornerShape(8.dp)) {
        Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = SnaparTurquoise, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun saiLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

package kz.snapar.app.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.ChatMessage
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.theme.SnaparDark
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun SaiScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onRouteGenerated: (TravelRoute) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val messages = remember {
        mutableStateListOf(
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
    val suggestions = listOf(
        saiLocal(language, "3 күн табиғат", "3 дня на природе", "3 days in nature"),
        saiLocal(language, "Отбасылық тур", "Семейный тур", "Family trip"),
        saiLocal(language, "60 000 ₸ бюджет", "Бюджет 60 000 ₸", "60,000 ₸ budget"),
        saiLocal(language, "Жаяу серуен", "Пеший маршрут", "Hiking trip"),
    )

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        messages += ChatMessage(clean, true)
        val routeRequest = listOf("күн", "день", "day", "тур", "route", "маршрут", "бюджет")
            .any { clean.contains(it, ignoreCase = true) }
        if (routeRequest) {
            val days = Regex("""\b([1-5])\b""").find(clean)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 3
            val route = SampleData.generateRoute(
                origin = saiLocal(language, "Алматы", "Алматы", "Almaty"),
                days = days,
                budget = if ("60" in clean) 60_000 else 75_000,
                interests = buildSet {
                    if (clean.contains("отбас", true) || clean.contains("сем", true) || clean.contains("family", true)) add("family")
                    if (clean.contains("фото", true) || clean.contains("photo", true)) add("photo")
                    add("nature")
                },
            )
            messages += ChatMessage(
                text = saiLocal(
                    language,
                    "Дайын! Жол, тамақ, демалыс және ауа райын ескеріп жоспар құрдым.",
                    "Готово! Я учёл дорогу, питание, отдых и погоду.",
                    "Ready! I included transport, food, rest stops and weather.",
                ),
                isUser = false,
                route = route,
            )
        } else {
            messages += ChatMessage(
                text = saiLocal(
                    language,
                    "Түсіндім. Күн санын, бюджет пен қызығушылықты жазыңыз, мен нақты маршрут ұсынамын.",
                    "Понял. Укажите количество дней, бюджет и интересы, и я предложу точный маршрут.",
                    "Got it. Tell me the number of days, budget and interests, and I will create a route.",
                ),
                isUser = false,
            )
        }
        input = ""
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
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
            }
            Box(
                Modifier
                    .size(50.dp)
                    .background(SnaparTurquoise, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("SAI Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("online · ${language.label}", color = SnaparTurquoise, fontSize = 12.sp)
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
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { suggestion ->
                Surface(
                    modifier = Modifier.clickable { input = suggestion },
                    color = Color(0xFF25384C),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Text(suggestion, Modifier.padding(horizontal = 14.dp, vertical = 9.dp), color = Color.White)
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
                    voiceLauncher.launch(intent)
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
private fun MessageBubble(message: ChatMessage, language: AppLanguage, onRoute: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!message.isUser) {
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .size(34.dp)
                    .background(SnaparTurquoise, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth(.82f)
                .heightIn(min = 48.dp),
            color = if (message.isUser) SnaparTurquoise else Color(0xFF22384D),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp,
            ),
        ) {
            Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(message.text, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                message.route?.let { route ->
                    Surface(color = Color(0xFF172A3B), shape = RoundedCornerShape(13.dp)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(route.title.value(language), color = Color.White, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("${route.days} ${saiLocal(language, "күн", "дн.", "days")}", color = SnaparTurquoise)
                                Text("${route.distanceKm} км", color = SnaparTurquoise)
                                Text(formatTenge(route.price), color = SnaparTurquoise)
                            }
                            Button(
                                onClick = onRoute,
                                colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Icon(Icons.Outlined.Map, null)
                                Spacer(Modifier.width(6.dp))
                                Text(saiLocal(language, "Толық жоспар", "Полный план", "Full plan"))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun saiLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

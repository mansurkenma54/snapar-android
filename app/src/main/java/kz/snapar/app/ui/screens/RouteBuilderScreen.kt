package kz.snapar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.LocalText
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun RouteBuilderScreen(
    language: AppLanguage,
    onBack: () -> Unit,
    onGenerated: (TravelRoute) -> Unit,
) {
    val labels = strings(language)
    var origin by remember { mutableStateOf(localBuilder(language, "Алматы", "Алматы", "Almaty")) }
    var days by remember { mutableFloatStateOf(3f) }
    var budget by remember { mutableFloatStateOf(60_000f) }
    var interests by remember { mutableStateOf(setOf("nature", "photo")) }
    var departureDate by remember { mutableStateOf("2026-06-20") }
    var transport by remember { mutableStateOf("public") }
    var lodging by remember { mutableStateOf("guest") }
    var pace by remember { mutableStateOf("balanced") }
    var validationError by remember { mutableStateOf("") }
    val interestOptions = listOf(
        "nature" to localBuilder(language, "Табиғат", "Природа", "Nature"),
        "family" to localBuilder(language, "Отбасылық", "Семейный", "Family"),
        "photo" to localBuilder(language, "Фото", "Фото", "Photo"),
        "adventure" to localBuilder(language, "Белсенді", "Активный", "Adventure"),
        "culture" to localBuilder(language, "Мәдениет", "Культура", "Culture"),
    )

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") }
                    Text(labels.routeBuilder, style = MaterialTheme.typography.titleLarge)
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    if (validationError.isNotBlank()) {
                        Text(validationError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Button(
                        onClick = {
                            val dateValid = Regex("""\d{4}-\d{2}-\d{2}""").matches(departureDate)
                            if (origin.isBlank() || interests.isEmpty() || !dateValid) {
                                validationError = localBuilder(
                                    language,
                                    "Қала, кемінде бір қызығушылық және YYYY-MM-DD күнін толтырыңыз.",
                                    "Заполните город, хотя бы один интерес и дату YYYY-MM-DD.",
                                    "Enter a city, at least one interest and a YYYY-MM-DD date.",
                                )
                                return@Button
                            }
                            validationError = ""
                            val transportExtra = when (transport) {
                                "car" -> 12_000
                                "tour" -> 18_000
                                else -> 0
                            }
                            val lodgingExtra = when (lodging) {
                                "hotel" -> 15_000 * days.toInt()
                                "camp" -> -5_000
                                else -> 0
                            }
                            val estimatedCost = (18_000 * days.toInt() + transportExtra + lodgingExtra)
                                .coerceAtLeast(15_000)
                                .coerceAtMost(budget.toInt())
                            val generated = SampleData.generateRoute(
                                    origin = origin.ifBlank { "Almaty" },
                                    days = days.toInt(),
                                    budget = estimatedCost,
                                    interests = interests,
                                ).copy(
                                    transport = choiceText(
                                        transport,
                                        "Қоғамдық көлік", "Общественный транспорт", "Public transport",
                                        "Жеке көлік", "Автомобиль", "Car",
                                        "Тур трансфері", "Тур-трансфер", "Tour transfer",
                                    ),
                                    lodging = choiceText(
                                        lodging,
                                        "Қонақ үй", "Гостевой дом", "Guesthouse",
                                        "Отель", "Отель", "Hotel",
                                        "Кемпинг", "Кемпинг", "Camping",
                                    ),
                                    pace = choiceText(
                                        pace,
                                        "Жайлы", "Спокойный", "Relaxed",
                                        "Теңгерімді", "Средний", "Balanced",
                                        "Белсенді", "Активный", "Active",
                                    ),
                                    departureDate = departureDate,
                                )
                            onGenerated(generated)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text(labels.generate, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(
                color = SnaparTurquoise.copy(alpha = .12f),
                shape = RoundedCornerShape(20.dp),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = SnaparPrimary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        localBuilder(
                            language,
                            "SAI бюджетіңізге, уақытыңызға және талғамыңызға сай толық жоспар жасайды.",
                            "SAI составит полный план под ваш бюджет, время и интересы.",
                            "SAI will build a complete plan around your budget, time and interests.",
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(labels.startCity) },
                leadingIcon = { Icon(Icons.Outlined.Place, null) },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
            )
            OutlinedTextField(
                value = departureDate,
                onValueChange = { departureDate = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localBuilder(language, "Шығу күні (YYYY-MM-DD)", "Дата выезда (YYYY-MM-DD)", "Departure date (YYYY-MM-DD)")) },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null) },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
            )

            Column {
                Text("${labels.days}: ${days.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Slider(
                    value = days,
                    onValueChange = { days = it },
                    valueRange = 1f..5f,
                    steps = 3,
                )
            }

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(labels.budget, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(formatTenge(budget.toInt()), color = SnaparPrimary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = budget,
                    onValueChange = { budget = it },
                    valueRange = 20_000f..250_000f,
                    steps = 22,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(labels.interests, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                interestOptions.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { option ->
                            FilterChip(
                                selected = option.first in interests,
                                onClick = {
                                    interests = if (option.first in interests) interests - option.first else interests + option.first
                                },
                                label = { Text(option.second) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SnaparTurquoise,
                                    selectedLabelColor = Color.White,
                                ),
                            )
                        }
                    }
                }
            }

            ChoiceSection(
                icon = Icons.Outlined.DirectionsBus,
                title = localBuilder(language, "Көлік", "Транспорт", "Transport"),
                selected = transport,
                options = listOf(
                    "public" to localBuilder(language, "Автобус", "Автобус", "Public"),
                    "car" to localBuilder(language, "Жеке көлік", "Автомобиль", "Car"),
                    "tour" to localBuilder(language, "Тур көлігі", "Тур-трансфер", "Tour van"),
                ),
                onSelect = { transport = it },
            )
            ChoiceSection(
                icon = Icons.Outlined.Hotel,
                title = localBuilder(language, "Қону", "Проживание", "Lodging"),
                selected = lodging,
                options = listOf(
                    "guest" to localBuilder(language, "Қонақ үй", "Гостевой дом", "Guesthouse"),
                    "hotel" to localBuilder(language, "Отель", "Отель", "Hotel"),
                    "camp" to localBuilder(language, "Кемпинг", "Кемпинг", "Camping"),
                ),
                onSelect = { lodging = it },
            )
            ChoiceSection(
                icon = Icons.Outlined.Speed,
                title = localBuilder(language, "Сапар ырғағы", "Темп поездки", "Travel pace"),
                selected = pace,
                options = listOf(
                    "relaxed" to localBuilder(language, "Жайлы", "Спокойный", "Relaxed"),
                    "balanced" to localBuilder(language, "Теңгерімді", "Средний", "Balanced"),
                    "active" to localBuilder(language, "Белсенді", "Активный", "Active"),
                ),
                onSelect = { pace = it },
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ChoiceSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selected: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = SnaparPrimary)
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            items(options) { option ->
                FilterChip(
                    selected = selected == option.first,
                    onClick = { onSelect(option.first) },
                    label = { Text(option.second) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SnaparTurquoise,
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }
    }
}

private fun choiceText(
    key: String,
    firstKk: String,
    firstRu: String,
    firstEn: String,
    secondKk: String,
    secondRu: String,
    secondEn: String,
    thirdKk: String,
    thirdRu: String,
    thirdEn: String,
): LocalText = when (key) {
    "car", "hotel", "balanced" -> LocalText(secondKk, secondRu, secondEn)
    "tour", "camp", "active" -> LocalText(thirdKk, thirdRu, thirdEn)
    else -> LocalText(firstKk, firstRu, firstEn)
}

private fun localBuilder(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Place
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
    val interestOptions = listOf(
        "nature" to localBuilder(language, "Табиғат", "Природа", "Nature"),
        "family" to localBuilder(language, "Отбасылық", "Семейный", "Family"),
        "photo" to localBuilder(language, "Фото", "Фото", "Photo"),
        "adventure" to localBuilder(language, "Белсенді", "Активный", "Adventure"),
        "culture" to localBuilder(language, "Мәдениет", "Культура", "Culture"),
    )

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
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
                        color = SnaparNavy,
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

            Column {
                Text("${labels.days}: ${days.toInt()}", fontWeight = FontWeight.Bold, color = SnaparNavy)
                Slider(
                    value = days,
                    onValueChange = { days = it },
                    valueRange = 1f..5f,
                    steps = 3,
                )
            }

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(labels.budget, fontWeight = FontWeight.Bold, color = SnaparNavy)
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
                Text(labels.interests, fontWeight = FontWeight.Bold, color = SnaparNavy)
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

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onGenerated(
                        SampleData.generateRoute(
                            origin = origin.ifBlank { "Almaty" },
                            days = days.toInt(),
                            budget = budget.toInt(),
                            interests = interests,
                        ),
                    )
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
}

private fun localBuilder(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

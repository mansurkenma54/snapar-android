package kz.snapar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun BusinessScreen(language: AppLanguage, onBack: () -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Back") }
                    Text(businessLocal(language, "Бизнес панелі", "Бизнес-панель", "Business dashboard"), style = MaterialTheme.typography.titleLarge)
                }
            }
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SectionTitle(businessLocal(language, "Аналитика", "Аналитика", "Analytics"))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BusinessMetric(Icons.Outlined.Visibility, "12.4K", businessLocal(language, "Көрілім", "Просмотры", "Views"), Modifier.weight(1f))
                BusinessMetric(Icons.Outlined.People, "842", businessLocal(language, "Қызықты", "Интерес", "Leads"), Modifier.weight(1f))
                BusinessMetric(Icons.Outlined.Payments, "6.2%", businessLocal(language, "Өтім", "Конверсия", "Conversion"), Modifier.weight(1f))
            }
            SectionTitle(businessLocal(language, "Жаңа орын қосу", "Добавить новое место", "Add a new venue"))
            OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text(businessLocal(language, "Атауы", "Название", "Name")) }, singleLine = true)
            OutlinedTextField(category, { category = it }, Modifier.fillMaxWidth(), label = { Text(businessLocal(language, "Санат", "Категория", "Category")) }, singleLine = true)
            OutlinedTextField(
                address,
                { address = it },
                Modifier.fillMaxWidth(),
                label = { Text(businessLocal(language, "Мекенжай / GPS", "Адрес / GPS", "Address / GPS")) },
                leadingIcon = { Icon(Icons.Outlined.LocationOn, null) },
                singleLine = true,
            )
            OutlinedTextField(
                price,
                { price = it.filter(Char::isDigit) },
                Modifier.fillMaxWidth(),
                label = { Text(businessLocal(language, "Орташа баға ₸", "Средняя цена ₸", "Average price ₸")) },
                leadingIcon = { Icon(Icons.Outlined.Payments, null) },
                singleLine = true,
            )
            OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text(businessLocal(language, "Сипаттама", "Описание", "Description")) }, minLines = 3)
            Card(colors = CardDefaults.cardColors(containerColor = SnaparTurquoise.copy(.1f)), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.PhotoLibrary, null, tint = SnaparPrimary)
                    Spacer(Modifier.padding(5.dp))
                    Column {
                        Text(businessLocal(language, "Фото және ұсыныстар", "Фото и предложения", "Photos and offers"), fontWeight = FontWeight.Bold, color = SnaparNavy)
                        Text(businessLocal(language, "Келесі нұсқада медиа менеджерге қосылады", "Будет доступно в медиаменеджере", "Available through the media manager"), color = SnaparMuted)
                    }
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        snackbar.showSnackbar(
                            if (name.isBlank()) businessLocal(language, "Орын атауын енгізіңіз", "Введите название", "Enter a venue name")
                            else businessLocal(language, "\"$name\" тексеруге жіберілді", "\"$name\" отправлено на проверку", "\"$name\" submitted for review"),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(businessLocal(language, "Орынды жариялау", "Опубликовать место", "Publish venue"))
            }
        }
    }
}

@Composable
private fun BusinessMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(value, fontWeight = FontWeight.Bold, color = SnaparNavy)
            Text(label, color = SnaparMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun businessLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

package kz.snapar.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.VenueListing
import kz.snapar.app.ui.SnaparState
import kz.snapar.app.ui.components.SectionTitle
import kz.snapar.app.ui.theme.SnaparMuted
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun BusinessScreen(state: SnaparState, onBack: () -> Unit) {
    val language = state.language
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            imageUri = uri.toString()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
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
                BusinessMetric(Icons.Outlined.Visibility, "${12 + state.venues.size * 3}.4K", businessLocal(language, "Көрілім", "Просмотры", "Views"), Modifier.weight(1f))
                BusinessMetric(Icons.Outlined.People, "${842 + state.venues.size * 75}", businessLocal(language, "Қызықты", "Интерес", "Leads"), Modifier.weight(1f))
                BusinessMetric(Icons.Outlined.Payments, "${6 + state.venues.size}.2%", businessLocal(language, "Өтім", "Конверсия", "Conversion"), Modifier.weight(1f))
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { imagePicker.launch(arrayOf("image/*")) },
                colors = CardDefaults.cardColors(containerColor = SnaparTurquoise.copy(.1f)),
                shape = RoundedCornerShape(16.dp),
            ) {
                if (imageUri.isNotBlank()) {
                    AsyncImage(
                        Uri.parse(imageUri),
                        "Venue photo",
                        Modifier.fillMaxWidth().height(190.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.PhotoLibrary, null, tint = SnaparPrimary)
                        Spacer(Modifier.padding(5.dp))
                        Column {
                            Text(businessLocal(language, "Орын фотосын таңдаңыз", "Выберите фото места", "Choose a venue photo"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(businessLocal(language, "Галереядан жүктеу", "Загрузить из галереи", "Upload from gallery"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    val valid = name.isNotBlank() && category.isNotBlank() && address.isNotBlank()
                    if (valid) {
                        state.upsertVenue(
                            VenueListing(
                                id = editingId ?: "venue-${System.currentTimeMillis()}",
                                name = name.trim(),
                                category = category.trim(),
                                address = address.trim(),
                                averagePrice = price.toIntOrNull() ?: 0,
                                description = description.trim(),
                                imageUri = imageUri,
                            ),
                        )
                    }
                    scope.launch {
                        snackbar.showSnackbar(
                            if (!valid) businessLocal(language, "Атауы, санаты және мекенжайын толтырыңыз", "Заполните название, категорию и адрес", "Fill in the name, category and address")
                            else businessLocal(language, "\"$name\" тексеруге жіберілді", "\"$name\" отправлено на проверку", "\"$name\" submitted for review"),
                        )
                    }
                    if (valid) {
                        name = ""
                        category = ""
                        address = ""
                        price = ""
                        description = ""
                        imageUri = ""
                        editingId = null
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(
                    if (editingId == null) businessLocal(language, "Орынды жариялау", "Опубликовать место", "Publish venue")
                    else businessLocal(language, "Өзгерісті сақтау", "Сохранить изменения", "Save changes"),
                )
            }
            if (state.venues.isNotEmpty()) {
                SectionTitle(businessLocal(language, "Менің орындарым", "Мои места", "My venues"))
                state.venues.forEach { venue ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            if (venue.imageUri.isNotBlank()) {
                                AsyncImage(
                                    Uri.parse(venue.imageUri),
                                    venue.name,
                                    Modifier.fillMaxWidth().height(130.dp),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(venue.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    businessLocal(language, "Тексерілуде", "На проверке", "In review"),
                                    color = SnaparPrimary,
                                )
                            }
                            Text(venue.category, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(venue.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (venue.averagePrice > 0) {
                                Text("${venue.averagePrice} ₸", fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                IconButton(onClick = {
                                    editingId = venue.id
                                    name = venue.name
                                    category = venue.category
                                    address = venue.address
                                    price = venue.averagePrice.takeIf { it > 0 }?.toString().orEmpty()
                                    description = venue.description
                                    imageUri = venue.imageUri
                                }) {
                                    Icon(Icons.Outlined.Edit, businessLocal(language, "Өңдеу", "Редактировать", "Edit"))
                                }
                                IconButton(onClick = { state.deleteVenue(venue.id) }) {
                                    Icon(Icons.Outlined.Delete, businessLocal(language, "Өшіру", "Удалить", "Delete"), tint = MaterialTheme.colorScheme.error)
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
private fun BusinessMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = SnaparTurquoise)
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun businessLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

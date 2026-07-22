package kz.snapar.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kz.snapar.app.data.SampleData
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.BookingSlot
import kz.snapar.app.model.Place
import kz.snapar.app.ui.strings
import kz.snapar.app.ui.theme.SnaparGold
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparTurquoise
import kz.snapar.app.ui.components.formatTenge
import kz.snapar.app.ui.components.pressScale

@Composable
fun BookingScreen(
    place: Place,
    language: AppLanguage,
    onBack: () -> Unit,
) {
    val labels = strings(language)
    val slots = remember(place.id) { SampleData.bookingSlotsFor(place) }
    var selectedSlot by remember { mutableStateOf(slots.first { it.available }) }
    var guests by remember { mutableIntStateOf(2) }
    var confirmed by remember { mutableStateOf(false) }

    val serviceFee = (selectedSlot.price * guests * 0.05).toInt().coerceAtLeast(500)
    val total = selectedSlot.price * guests + serviceFee
    val bookingId = remember { "SNP-${(100_000..999_999).random()}" }

    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    labels.booking,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    color = SnaparGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        labels.bookingDemo,
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = SnaparGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
        },
    ) { padding ->
        if (confirmed) {
            BookingConfirmedScreen(
                place = place,
                language = language,
                slot = selectedSlot,
                guests = guests,
                total = total,
                bookingId = bookingId,
                onBack = onBack,
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = place.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(width = 110.dp, height = 100.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(place.name.value(language), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(place.region.value(language), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Surface(color = SnaparTurquoise.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    "${formatTenge(place.averageCost)} / ${bookLocal(language, "адам", "чел.", "guest")}",
                                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = SnaparPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.EventAvailable, null, tint = SnaparTurquoise, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(bookLocal(language, "Күн таңдаңыз", "Выберите дату", "Select date"), fontWeight = FontWeight.Bold)
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(slots) { slot ->
                            val active = slot == selectedSlot
                            val clickable = slot.available
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
                                    .pressScale(enabled = clickable)
                                    .then(if (clickable) Modifier.clickable { selectedSlot = slot } else Modifier),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        active -> SnaparPrimary
                                        !clickable -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                ),
                                elevation = CardDefaults.cardElevation(if (active) 4.dp else 1.dp),
                            ) {
                                Column(
                                    Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        slot.dateLabel.value(language),
                                        color = if (active) Color.White else if (!clickable) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        if (!clickable) bookLocal(language, "Толған", "Занято", "Full") else formatTenge(slot.price),
                                        color = if (active) Color.White.copy(.85f) else if (!clickable) MaterialTheme.colorScheme.onSurfaceVariant else SnaparTurquoise,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.People, null, tint = SnaparTurquoise, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(bookLocal(language, "Қонақтар саны", "Количество гостей", "Number of guests"), fontWeight = FontWeight.Bold)
                                Text("${guests} ${labels.guests}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(
                                onClick = { if (guests > 1) guests-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            ) {
                                Icon(Icons.Outlined.Remove, null, tint = if (guests > 1) SnaparPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            AnimatedContent(
                                targetState = guests,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        slideInVertically { -it } + fadeIn() togetherWith
                                            slideOutVertically { it } + fadeOut()
                                    } else {
                                        slideInVertically { it } + fadeIn() togetherWith
                                            slideOutVertically { -it } + fadeOut()
                                    } using SizeTransform(clip = false)
                                },
                                label = "guest-count",
                            ) { count ->
                                Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SnaparNavy)
                            }
                            IconButton(
                                onClick = { if (guests < 8) guests++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SnaparTurquoise, CircleShape),
                            ) {
                                Icon(Icons.Outlined.Add, null, tint = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(bookLocal(language, "Төлем есебі", "Расчёт стоимости", "Price breakdown"), fontWeight = FontWeight.Bold)
                        HorizontalDivider()
                        PriceRow(
                            "${formatTenge(selectedSlot.price)} × $guests ${labels.guests}",
                            formatTenge(selectedSlot.price * guests),
                        )
                        PriceRow(
                            bookLocal(language, "Сервис ақысы (5%)", "Сервисный сбор (5%)", "Service fee (5%)"),
                            formatTenge(serviceFee),
                        )
                        HorizontalDivider()
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(bookLocal(language, "Жалпы:", "Итого:", "Total:"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(formatTenge(total), fontWeight = FontWeight.Bold, color = SnaparPrimary, fontSize = 18.sp)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { confirmed = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SnaparTurquoise),
                ) {
                    Icon(Icons.Outlined.ConfirmationNumber, null)
                    Spacer(Modifier.width(8.dp))
                    Text(labels.bookNow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    bookLocal(language, "Kaspi Pay · Halyk ePay қолдайды (demo режим)", "Поддерживает Kaspi Pay · Halyk ePay (demo режим)", "Supports Kaspi Pay · Halyk ePay (demo mode)"),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun BookingConfirmedScreen(
    place: Place,
    language: AppLanguage,
    slot: BookingSlot,
    guests: Int,
    total: Int,
    bookingId: String,
    onBack: () -> Unit,
) {
    val labels = strings(language)
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .size(72.dp)
                .background(SnaparTurquoise.copy(.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.CheckCircle, null, tint = SnaparTurquoise, modifier = Modifier.size(44.dp))
        }
        Text(labels.bookingConfirmed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Column(Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = place.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop,
                )
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    QrTicketGrid()
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    TicketRow(bookLocal(language, "Орын", "Место", "Destination"), place.name.value(language))
                    TicketRow(bookLocal(language, "Күн", "Дата", "Date"), slot.dateLabel.value(language))
                    TicketRow(bookLocal(language, "Қонақтар", "Гости", "Guests"), "$guests ${labels.guests}")
                    TicketRow(bookLocal(language, "Жалпы", "Итого", "Total"), formatTenge(total))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(bookLocal(language, "Брондау №", "Бронь №", "Booking #"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(bookingId, fontWeight = FontWeight.Bold, color = SnaparPrimary)
                        }
                        Surface(color = SnaparGold.copy(.15f), shape = RoundedCornerShape(8.dp)) {
                            Text(labels.bookingDemo, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = SnaparGold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SnaparPrimary),
        ) {
            Text(bookLocal(language, "Басты бетке оралу", "Вернуться на главную", "Return to home"), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun QrTicketGrid() {
    val cells = remember {
        (0 until 100).map { listOf(true, false, true, false).random() }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F7F6), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            QrCorner()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                for (row in 0 until 6) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (col in 0 until 8) {
                            val filled = cells.getOrElse(row * 8 + col) { false }
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .background(
                                        if (filled) Color(0xFF0D2137) else Color.Transparent,
                                        RoundedCornerShape(1.dp),
                                    ),
                            )
                        }
                    }
                }
            }
            QrCorner()
        }
        Spacer(Modifier.height(6.dp))
        Text("SNAPAR · e-TICKET", color = Color(0xFF516274), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
    }
}

@Composable
private fun QrCorner() {
    Box(
        Modifier
            .size(28.dp)
            .border(3.dp, Color(0xFF0D2137), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(14.dp)
                .background(Color(0xFF0D2137), RoundedCornerShape(2.dp)),
        )
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TicketRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

private fun bookLocal(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
    AppLanguage.Kazakh -> kk
    AppLanguage.Russian -> ru
    AppLanguage.English -> en
}

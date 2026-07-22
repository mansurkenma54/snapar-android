package kz.snapar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.snapar.app.model.AppScreen
import kz.snapar.app.ui.UiStrings
import kz.snapar.app.ui.theme.SnaparNavy
import kz.snapar.app.ui.theme.SnaparPrimary
import kz.snapar.app.ui.theme.SnaparSurface
import kz.snapar.app.ui.theme.SnaparTurquoise

@Composable
fun SnaparTopBar(
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCameraClick: () -> Unit,
    unreadNotifications: Int = 0,
) {
    val infinite = rememberInfiniteTransition(label = "logo")
    val logoAlpha by infinite.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logo-alpha",
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        shadowElevation = 3.dp,
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
                }
                // Logo — gradient text effect via Box
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "✦ Snapar",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        modifier = Modifier.graphicsLayer { alpha = logoAlpha },
                        color = SnaparTurquoise,
                    )
                }
                Row {
                    IconButton(onClick = onCameraClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.radialGradient(listOf(SnaparTurquoise.copy(.12f), Color.Transparent)),
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = "GeoSnap", tint = SnaparPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications > 0) {
                                    Badge { Text(unreadNotifications.coerceAtMost(9).toString()) }
                                }
                            },
                        ) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0x0F000000))
        }
    }
}

private data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
)

@Composable
fun SnaparBottomBar(
    selected: AppScreen,
    labels: UiStrings,
    onSelect: (AppScreen) -> Unit,
    reducedMotion: Boolean = false,
) {
    val items = listOf(
        NavItem(AppScreen.Discover, labels.discover, Icons.Outlined.Explore, Icons.Rounded.Explore),
        NavItem(AppScreen.Routes, labels.routes, Icons.Outlined.Map, Icons.Rounded.Map),
        NavItem(AppScreen.Passport, labels.passport, Icons.Rounded.MenuBook, Icons.Rounded.MenuBook),
        NavItem(AppScreen.Profile, labels.profile, Icons.Outlined.Person, Icons.Rounded.Person),
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        items.take(2).forEach { item -> NavItemView(item, selected, onSelect) }
        NavigationBarItem(
            selected = selected == AppScreen.Sai,
            onClick = { onSelect(AppScreen.Sai) },
            icon = { PulsingSaiIcon(animated = !reducedMotion) },
            label = { Text(labels.sai, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedTextColor = SnaparPrimary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        items.drop(2).forEach { item -> NavItemView(item, selected, onSelect) }
    }
}

@Composable
private fun RowScope.NavItemView(
    item: NavItem,
    selected: AppScreen,
    onSelect: (AppScreen) -> Unit,
) {
    val active = selected == item.screen
    NavigationBarItem(
        selected = active,
        onClick = { onSelect(item.screen) },
        icon = {
            Icon(
                imageVector = if (active) item.activeIcon else item.icon,
                contentDescription = item.label,
            )
        },
        label = { Text(item.label, fontSize = 10.sp, maxLines = 1) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = SnaparTurquoise.copy(alpha = 0.14f),
            selectedIconColor = SnaparTurquoise,
            selectedTextColor = SnaparPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
fun PulsingSaiIcon(modifier: Modifier = Modifier, animated: Boolean = true) {
    val transition = rememberInfiniteTransition(label = "sai-pulse")
    val scale by transition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sai-scale",
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow-alpha",
    )
    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Outer glow ring
        if (animated) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(SnaparTurquoise.copy(alpha = glowAlpha), Color.Transparent),
                        ),
                        CircleShape,
                    ),
            )
        }
        // Main circle
        Box(
            modifier = Modifier
                .scale(if (animated) scale else 1f)
                .size(44.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(SnaparTurquoise, SnaparPrimary),
                    ),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = "SAI", tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        if (action != null) {
            TextButton(onClick = onAction) {
                Text(action, color = SnaparTurquoise)
            }
        }
    }
}

fun formatTenge(amount: Int): String = "%,d ₸".format(amount).replace(',', ' ')
fun formatMinutes(minutes: Int): String =
    if (minutes < 60) "$minutes мин" else "${minutes / 60} сағ ${minutes % 60} мин"

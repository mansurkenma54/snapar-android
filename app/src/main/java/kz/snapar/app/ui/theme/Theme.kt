package kz.snapar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SnaparPrimary = Color(0xFF006A68)
val SnaparTurquoise = Color(0xFF14C8C4)
val SnaparNavy = Color(0xFF08345A)
val SnaparGold = Color(0xFFFF9B58)
val SnaparSurface = Color(0xFFF4FBF9)
val SnaparCard = Color.White
val SnaparMuted = Color(0xFF3C4948)
val SnaparDark = Color(0xFF101E2D)
val SnaparDanger = Color(0xFFBA1A1A)

private val colors = lightColorScheme(
    primary = SnaparPrimary,
    onPrimary = Color.White,
    primaryContainer = SnaparTurquoise,
    onPrimaryContainer = Color(0xFF003D3C),
    secondary = Color(0xFF3E6089),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFAFD1FF),
    tertiary = Color(0xFF964908),
    tertiaryContainer = SnaparGold,
    error = SnaparDanger,
    background = SnaparSurface,
    onBackground = SnaparNavy,
    surface = SnaparSurface,
    onSurface = SnaparNavy,
    surfaceVariant = Color(0xFFE3EAE8),
    onSurfaceVariant = SnaparMuted,
    outline = Color(0xFF6C7A79),
)

private val darkColors = darkColorScheme(
    primary = SnaparTurquoise,
    onPrimary = Color(0xFF003735),
    primaryContainer = SnaparPrimary,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9FCBFF),
    onSecondary = Color(0xFF063257),
    tertiary = SnaparGold,
    background = SnaparDark,
    onBackground = Color(0xFFE6F0EF),
    surface = Color(0xFF132536),
    onSurface = Color(0xFFE6F0EF),
    surfaceVariant = Color(0xFF283A49),
    onSurfaceVariant = Color(0xFFC0CFCD),
    outline = Color(0xFF899896),
)

private fun typography(scale: Float) = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = (32 * scale).sp,
        lineHeight = (38 * scale).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = (20 * scale).sp,
        lineHeight = (27 * scale).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = (23 * scale).sp,
        lineHeight = (29 * scale).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = (16 * scale).sp,
        lineHeight = (22 * scale).sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = (16 * scale).sp,
        lineHeight = (24 * scale).sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = (14 * scale).sp,
        lineHeight = (21 * scale).sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = (12 * scale).sp,
        letterSpacing = 0.35.sp,
    ),
)

@Composable
fun SnaparTheme(
    darkTheme: Boolean,
    largeText: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else colors,
        typography = typography(if (largeText) 1.13f else 1f),
        content = content,
    )
}

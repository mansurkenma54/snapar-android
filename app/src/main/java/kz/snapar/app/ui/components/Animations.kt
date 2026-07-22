package kz.snapar.app.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.IntOffset

// Ең жұмсақ spring анимация
val SnaparSpring = spring<Float>(dampingRatio = 0.65f, stiffness = 380f)

// Экран ауысу (slide) анимациясы үшін IntOffset нұсқасы
val SnaparSlideSpring = spring<IntOffset>(dampingRatio = 0.65f, stiffness = 380f)

// Кіру анимациясы: slide + fade
fun snaparEnterTween(delayMs: Int = 0) = tween<Float>(
    durationMillis = 420,
    delayMillis = delayMs,
    easing = FastOutSlowInEasing,
)

// Shimmer жарық эффектісі (загрузка кезінде)
@Composable
fun shimmerBrush(baseColor: Color = Color(0xFFE8EEEC)): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-x",
    )
    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            baseColor.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.85f),
            baseColor.copy(alpha = 0.55f),
            baseColor,
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 300f, 200f),
    )
}

// Басқанда масштаб кішірейеді (press feedback)
fun Modifier.pressScale(
    pressedScale: Float = 0.965f,
    enabled: Boolean = true,
): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "press-scale",
    )
    this
        .scale(if (enabled) scale else 1f)
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                },
            )
        }
}

// SAI батырмасының жылтыр (glow) сақинасы
@Composable
fun Modifier.saiGlow(): Modifier {
    val transition = rememberInfiniteTransition(label = "sai-glow")
    val alpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-alpha",
    )
    return this.background(
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF14C8C4).copy(alpha = alpha),
                Color.Transparent,
            ),
        ),
    )
}

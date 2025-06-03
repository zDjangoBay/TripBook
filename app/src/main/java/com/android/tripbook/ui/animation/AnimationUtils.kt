package com.android.tripbook.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Utility class for animations in the app
 */
object AnimationUtils {

    /**
     * Creates a fade in/out with slide animation
     */
    fun enterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(300)
        ) + slideInVertically(
            animationSpec = tween(300, easing = EaseOutQuart),
            initialOffsetY = { it / 5 }
        )
    }

    /**
     * Creates a fade out with slide animation
     */
    fun exitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(300)
        ) + slideOutVertically(
            animationSpec = tween(300, easing = EaseInQuart),
            targetOffsetY = { -it / 5 }
        )
    }

    /**
     * Creates a slide in from right animation
     */
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            animationSpec = tween(300, easing = EaseOutQuart),
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = tween(300))
    }

    /**
     * Creates a slide out to left animation
     */
    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            animationSpec = tween(300, easing = EaseInQuart),
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = tween(300))
    }

    /**
     * Creates a spring entry animation
     */
    fun springInTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(300)
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.8f
        )
    }

    /**
     * Creates a pulse animation modifier
     */
    @Composable
    fun pulseAnimation(
        pulseFraction: Float = 1.2f,
        duration: Int = 1000
    ): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = pulseFraction,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        return Modifier.scale(scale)
    }

    /**
     * Creates a floating animation modifier
     */
    @Composable
    fun floatingAnimation(
        offsetY: Int = 20,
        duration: Int = 2000
    ): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "float")
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "offset"
        )

        return Modifier.offset {
            IntOffset(0, (offset * offsetY).roundToInt())
        }
    }

    /**
     * Creates a rotating animation modifier
     */
    @Composable
    fun rotateAnimation(
        degrees: Float = 10f,
        duration: Int = 3000
    ): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "rotate")
        val rotation by infiniteTransition.animateFloat(
            initialValue = -degrees,
            targetValue = degrees,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        )

        return Modifier.rotate(rotation)
    }

    /**
     * Creates a spring animation modifier for buttons
     */
    @Composable
    fun springButtonAnimation(): Modifier {
        val scale = remember { Animatable(1f) }

        return Modifier.scale(scale.value)
    }

    /**
     * Creates a wave animation modifier
     */
    @Composable
    fun waveAnimation(
        amplitude: Float = 10f,
        frequency: Float = 0.1f,
        duration: Int = 3000
    ): Modifier {
        val infiniteTransition = rememberInfiniteTransition(label = "wave")
        val phase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * Math.PI.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "phase"
        )

        return Modifier.graphicsLayer {
            translationY = amplitude * sin(frequency * phase)
        }
    }

    /**
     * Creates a shimmer animation for loading states
     */
    @Composable
    fun shimmerEffect(): Modifier {
        val shimmer = rememberInfiniteTransition(label = "shimmer")
        val alpha by shimmer.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        return Modifier.graphicsLayer {
            this.alpha = alpha
        }
    }
}

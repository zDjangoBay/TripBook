package com.android.tripbook.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Utility class for animations
 */
object AnimationUtils {
    /**
     * Fade in animation
     */
    @Composable
    fun FadeInAnimation(
        visible: Boolean = true,
        initialAlpha: Float = 0f,
        targetAlpha: Float = 1f,
        durationMillis: Int = 300,
        content: @Composable () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                initialAlpha = initialAlpha,
                animationSpec = tween(durationMillis)
            ),
            exit = fadeOut(
                targetAlpha = initialAlpha,
                animationSpec = tween(durationMillis)
            )
        ) {
            content()
        }
    }
    
    /**
     * Slide in animation
     */
    @Composable
    fun SlideInAnimation(
        visible: Boolean = true,
        initialOffsetX: Int = 300,
        durationMillis: Int = 300,
        content: @Composable () -> Unit
    ) {
        val density = LocalDensity.current
        
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { with(density) { initialOffsetX.dp.roundToPx() } },
                animationSpec = tween(durationMillis)
            ) + fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(durationMillis)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { with(density) { -initialOffsetX.dp.roundToPx() } },
                animationSpec = tween(durationMillis)
            ) + fadeOut(
                targetAlpha = 0f,
                animationSpec = tween(durationMillis)
            )
        ) {
            content()
        }
    }
    
    /**
     * Scale animation
     */
    @Composable
    fun ScaleAnimation(
        visible: Boolean = true,
        initialScale: Float = 0.8f,
        durationMillis: Int = 300,
        content: @Composable () -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = initialScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(durationMillis)
            ),
            exit = scaleOut(
                targetScale = initialScale,
                animationSpec = tween(durationMillis)
            ) + fadeOut(
                targetAlpha = 0f,
                animationSpec = tween(durationMillis)
            )
        ) {
            content()
        }
    }
    
    /**
     * Staggered animation for lists
     */
    @Composable
    fun StaggeredAnimation(
        index: Int,
        delayPerItem: Int = 100,
        content: @Composable () -> Unit
    ) {
        var visible by remember { mutableStateOf(false) }
        
        LaunchedEffect(key1 = true) {
            delay(delayPerItem.toLong() * index)
            visible = true
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(300)
            ) + expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = fadeOut(
                targetAlpha = 0f,
                animationSpec = tween(300)
            ) + shrinkVertically(
                animationSpec = tween(300)
            )
        ) {
            content()
        }
    }
}

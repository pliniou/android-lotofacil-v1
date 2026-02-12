package com.cebolao.lotofacil.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.theme.LocalAnimationEnabled
import kotlinx.coroutines.delay

@Composable
fun AnimateOnEntry(
    visible: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    initialAlpha: Float = 0f,
    finalAlpha: Float = 1f,
    initialOffsetY: Dp = 16.dp,
    finalOffsetY: Dp = 0.dp,
    animationSpec: Int = 500,
    delayMillis: Long = 0L,
    content: @Composable (modifier: Modifier) -> Unit
) {
    val animationsEnabled = LocalAnimationEnabled.current

    if (!animationsEnabled) {
        val alpha = if (visible) finalAlpha else initialAlpha
        val offsetY = if (visible) finalOffsetY else initialOffsetY
        content(
            modifier
                .alpha(alpha)
                .offset(y = offsetY)
        )
        return
    }

    val transitionState = remember { MutableTransitionState(initialState = false) }
    LaunchedEffect(visible, delayMillis) {
        if (visible) {
            if (delayMillis > 0) {
                delay(delayMillis)
            }
            transitionState.targetState = true
        } else {
            transitionState.targetState = false
        }
    }

    val transition = updateTransition(transitionState, label = "entryTransition")

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = animationSpec) },
        label = "alphaTransition"
    ) { if (it) finalAlpha else initialAlpha }

    val offsetY by transition.animateDp(
        transitionSpec = { tween(durationMillis = animationSpec) },
        label = "offsetYTransition"
    ) { if (it) finalOffsetY else initialOffsetY }

    content(
        modifier
            .alpha(alpha)
            .offset(y = offsetY)
    )
}

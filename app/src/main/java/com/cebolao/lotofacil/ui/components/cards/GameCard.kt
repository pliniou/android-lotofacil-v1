package com.cebolao.lotofacil.ui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.components.NumberBall
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameCard(
    game: LotofacilGame,
    modifier: Modifier = Modifier,
    onAnalyzeClick: () -> Unit,
    onShareClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    pinIcon: ImageVector = Icons.Default.PushPin,
    pinIconOutlined: ImageVector = Icons.Outlined.PushPin,
    deleteIcon: ImageVector = Icons.Default.Delete,
    shareIcon: ImageVector = Icons.Default.Share,
    analyzeIcon: ImageVector = Icons.Default.Analytics
) {
    val haptic = LocalHapticFeedback.current
    val colors = MaterialTheme.colorScheme

    // Cálculos otimizados
    val sortedNumbers by remember(game.numbers) {
        derivedStateOf { game.numbers.sorted() }
    }

    val isPinned = game.isPinned

    // Data/hora formatada
    val formattedDate = remember(game.creationTimestamp) {
        val sdf = SimpleDateFormat("dd/MM/yy · HH:mm", Locale("pt", "BR"))
        sdf.format(Date(game.creationTimestamp))
    }

    // ── Animações de pin ──
    val elevation by animateDpAsState(
        if (isPinned) AppCardDefaults.pinnedElevation else AppCardDefaults.elevation,
        tween(AppTheme.motion.durationElevationMs),
        label = "elevation"
    )

    val containerColor by animateColorAsState(
        if (isPinned) colors.secondaryContainer else colors.surface,
        tween(AppTheme.motion.durationColorChangeMs),
        label = "containerColor"
    )

    // Borda animada ao fixar
    val borderWidth by animateDpAsState(
        if (isPinned) 1.5.dp else 0.dp,
        tween(AppTheme.motion.durationColorChangeMs),
        label = "borderWidth"
    )
    val borderColor by animateColorAsState(
        if (isPinned) colors.primary.copy(alpha = 0.4f)
        else colors.outline.copy(alpha = 0f),
        tween(AppTheme.motion.durationColorChangeMs),
        label = "borderColor"
    )

    // Animação de escala "bounce" ao fixar/desafixar
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isPinned) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            initialVelocity = -0.15f // Começa "comprimido" e quica
        )
    }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleAnim.value)
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = MaterialTheme.shapes.medium
            ),
        backgroundColor = containerColor,
        elevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            // ── Header: badge fixado + data/hora ──
            GameCardHeader(
                isPinned = isPinned,
                formattedDate = formattedDate
            )

            // ── Números ──
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                maxItemsInEachRow = 5
            ) {
                for (number in sortedNumbers) {
                    NumberBall(
                        number = number,
                        size = AppSize.numberBallSmall
                    )
                }
            }

            // ── Ações ──
            GameCardActions(
                isPinned = isPinned,
                onAnalyzeClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onAnalyzeClick()
                },
                onShareClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onShareClick()
                },
                onPinClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPinClick()
                },
                onDeleteClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDeleteClick()
                },
                pinIcon = pinIcon,
                pinIconOutlined = pinIconOutlined,
                deleteIcon = deleteIcon,
                shareIcon = shareIcon,
                analyzeIcon = analyzeIcon
            )
        }
    }
}

/**
 * Header do card — exibe badge "📌 Fixado" com animação e data/hora de criação.
 */
@Composable
private fun GameCardHeader(
    isPinned: Boolean,
    formattedDate: String
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Badge "Fixado" animada
        AnimatedVisibility(
            visible = isPinned,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start
            ) + fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(id = R.string.pinned_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Data/hora de criação
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = colors.outline,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = colors.outline
            )
        }
    }
}

@Composable
private fun GameCardActions(
    isPinned: Boolean,
    onAnalyzeClick: () -> Unit,
    onShareClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    pinIcon: ImageVector,
    pinIconOutlined: ImageVector,
    deleteIcon: ImageVector,
    shareIcon: ImageVector,
    analyzeIcon: ImageVector
) {
    val colors = MaterialTheme.colorScheme

    // Rotação do ícone de pin (45° quando fixado → 0° quando solto)
    val pinRotation by animateFloatAsState(
        targetValue = if (isPinned) 0f else 45f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pinRotation"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            IconButton(
                onClick = onPinClick,
                modifier = Modifier.size(AppSize.touchTargetMinimum)
            ) {
                Icon(
                    imageVector = if (isPinned) pinIcon else pinIconOutlined,
                    contentDescription = if (isPinned) stringResource(id = R.string.unpin_game) else stringResource(id = R.string.pin_game),
                    tint = if (isPinned) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(AppSize.iconMedium)
                        .graphicsLayer { rotationZ = pinRotation }
                )
            }
            IconButton(
                onClick = onShareClick,
                modifier = Modifier.size(AppSize.touchTargetMinimum)
            ) {
                Icon(
                    shareIcon,
                    contentDescription = stringResource(id = R.string.cd_share_game),
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(AppSize.iconMedium)
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(AppSize.touchTargetMinimum)
            ) {
                Icon(
                    deleteIcon,
                    contentDescription = stringResource(id = R.string.delete_game),
                    tint = colors.error,
                    modifier = Modifier.size(AppSize.iconMedium)
                )
            }
        }
        TextButton(onClick = onAnalyzeClick) {
            Icon(
                analyzeIcon,
                null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = colors.primary
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                stringResource(id = R.string.analyze_button),
                color = colors.primary
            )
        }
    }
}

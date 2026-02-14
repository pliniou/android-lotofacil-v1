package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import com.cebolao.lotofacil.viewmodels.GenerationUiState
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GenerationActionsPanel(
    generationState: GenerationUiState,
    isDataSyncing: Boolean = false,
    activeFiltersCount: Int = 0,
    isCombinationPossible: Boolean = true,
    onGenerate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val options = remember { listOf(1, 2, 3, 5, 7, 9, 10, 12, 15, 20) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val quantity = options[selectedIndex]
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")) }
    val isLoading = generationState is GenerationUiState.Loading
    
    // Validation states
    val isQuantityValid = quantity > 0
    val hasActiveFilters = activeFiltersCount > 0
    val isGenerateEnabled = isQuantityValid &&
        hasActiveFilters &&
        isCombinationPossible &&
        !isLoading &&
        !isDataSyncing
    
    // Button color animation
    val buttonContainerColor by animateColorAsState(
        targetValue = if (isGenerateEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300),
        label = "buttonColor"
    )
    val buttonContentColor by animateColorAsState(
        targetValue = if (isGenerateEnabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "buttonContentColor"
    )

    AppCard(
        modifier = modifier.fillMaxWidth(),
        variant = CardVariant.Surfaced
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.game_quantity), style = MaterialTheme.typography.titleMedium)
                QuantitySelector(
                    quantity = quantity,
                    onDecrement = {
                        if (selectedIndex > 0) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex--
                        }
                    },
                    onIncrement = {
                        if (selectedIndex < options.lastIndex) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex++
                        }
                    },
                    isDecrementEnabled = selectedIndex > 0 && !isLoading && !isDataSyncing,
                    isIncrementEnabled = selectedIndex < options.lastIndex && !isLoading && !isDataSyncing
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            
            // Cost section with breakdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.total_cost),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = currencyFormat.format(
                            LotofacilConstants.GAME_COST.multiply(BigDecimal(quantity))
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Cost breakdown explanation
                Text(
                    text = stringResource(
                        id = R.string.cost_calculation,
                        quantity,
                        LotofacilConstants.GAME_COST.toPlainString()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGenerate(quantity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSize.buttonHeightDefault)
                    .testTag(AppTestTags.FiltersGenerateButton),
                enabled = isGenerateEnabled,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor
                )
            ) {
                AnimatedContent(
                    targetState = isLoading,
                    label = "GenerateButtonContent"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(AppSize.iconMedium),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                pluralStringResource(
                                    id = R.plurals.generate_games_label,
                                    count = quantity,
                                    quantity
                                )
                            )
                        }
                    }
                }
            }
            
            // Error message when button is disabled
            if (!isGenerateEnabled) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = when {
                        isDataSyncing -> stringResource(R.string.generate_button_disabled_syncing)
                        !isQuantityValid -> stringResource(R.string.generate_button_disabled_no_quantity)
                        !hasActiveFilters -> stringResource(R.string.generate_button_disabled_no_filters)
                        !isCombinationPossible -> stringResource(R.string.generate_button_disabled_impossible_filters)
                        else -> stringResource(R.string.generate_button_disabled_invalid_filters)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    isDecrementEnabled: Boolean,
    isIncrementEnabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        IconButton(
            onClick = onDecrement,
            enabled = isDecrementEnabled,
            modifier = Modifier.testTag(AppTestTags.FiltersQuantityDecrease)
        ) {
            Icon(Icons.Filled.Remove, stringResource(id = R.string.decrease_quantity))
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.widthIn(min = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        IconButton(
            onClick = onIncrement,
            enabled = isIncrementEnabled,
            modifier = Modifier.testTag(AppTestTags.FiltersQuantityIncrease)
        ) {
            Icon(Icons.Filled.Add, stringResource(id = R.string.increase_quantity))
        }
    }
}

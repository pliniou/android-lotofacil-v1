package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSize
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import com.cebolao.lotofacil.ui.testtags.AppTestTags

@Composable
fun CheckerScrollableActions(
    selectedCount: Int,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onClearClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    shape: Shape = MaterialTheme.shapes.small
) {
    val hapticFeedback = LocalHapticFeedback.current
    val resolvedHeight = height ?: AppTheme.sizes.buttonHeightDefault
    
    Column(
        modifier = modifier.padding(vertical = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedCount > 0) {
                OutlinedButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClearClick()
                    },
                    modifier = Modifier
                        .height(resolvedHeight)
                        .testTag(AppTestTags.CheckerClearButton),
                    shape = shape
                ) {
                    Text(
                        text = stringResource(id = R.string.clear_selection_button),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            
            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCheckClick()
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(resolvedHeight)
                    .testTag(AppTestTags.CheckerCheckButton),
                shape = shape
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppSize.iconSmall),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.check_game_button),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

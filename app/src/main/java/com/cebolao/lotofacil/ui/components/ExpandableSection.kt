package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.iconSmall

@Composable
fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    isExpandedByDefault: Boolean = true,
    content: @Composable () -> Unit
) {
    val isExpanded = remember(isExpandedByDefault) { mutableStateOf(isExpandedByDefault) }
    val interactionSource = remember { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    
    val expandedState = if (isExpanded.value) stringResource(R.string.expanded_state) else stringResource(R.string.collapsed_state)
    val sectionDescription = stringResource(
        R.string.expandable_section_description,
        title,
        expandedState
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource, indication = null) { 
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    isExpanded.value = !isExpanded.value 
                }
                .focusable(interactionSource = interactionSource)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter || keyEvent.key == Key.Spacebar) {
                        if (keyEvent.type == KeyEventType.KeyUp) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            isExpanded.value = !isExpanded.value
                            true
                        } else false
                    } else false
                }
                .padding(AppSpacing.md)
                .semantics {
                    contentDescription = sectionDescription
                    role = Role.Button
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(iconSmall())
            )
        }
        
        AnimatedVisibility(
            visible = isExpanded.value,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
            ) {
                content()
            }
        }
    }
}

package com.cebolao.lotofacil.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.text.AppStrings
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppColorRoles
import com.cebolao.lotofacil.ui.theme.AppSpacing

@Composable
fun GreetingSection(
    modifier: Modifier = Modifier,
    userName: String = stringResource(AppStrings.Greetings.defaultUser),
    nextDrawAccumulated: Boolean = false,
    isDrawDay: Boolean = false,
    lastUpdateTime: String? = null
) {
    val insightRes =
        when {
            nextDrawAccumulated -> AppStrings.Greetings.insightAccumulated
            isDrawDay -> AppStrings.Greetings.todayContest
            else -> AppStrings.Greetings.insightDefault
        }
    val insightIcon: ImageVector =
        if (nextDrawAccumulated) Icons.Default.Star else Icons.Outlined.Lightbulb

    AnimateOnEntry(
        delayMillis = AppAnimationConstants.Delays.Minimal.toLong()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = insightIcon,
                contentDescription = null,
                tint = if (nextDrawAccumulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                Text(
                    text = stringResource(AppStrings.Greetings.helloName, userName),
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColorRoles.bodyText(),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(insightRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColorRoles.supportingText()
                )
                if (lastUpdateTime != null) {
                    Text(
                        text = stringResource(R.string.last_update_status, lastUpdateTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColorRoles.subtleText()
                    )
                }
            }
        }
    }
}

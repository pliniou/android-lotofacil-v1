package com.cebolao.lotofacil.ui.screens.home

import android.content.res.Configuration
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.components.AnimateOnEntry
import com.cebolao.lotofacil.ui.theme.AppAnimationConstants
import com.cebolao.lotofacil.ui.theme.AppSpacing
import java.time.LocalTime
import androidx.compose.ui.unit.dp

@Composable
fun GreetingSection(
    modifier: Modifier = Modifier,
    userName: String = stringResource(R.string.greeting_user),
    nextDrawAccumulated: Boolean = false,
    isDrawDay: Boolean = false,
    lastUpdateTime: String? = null
) {
    val currentHour = remember { LocalTime.now().hour }
    val greetingRes = remember(currentHour) {
        when (currentHour) {
            in 5..11 -> R.string.greeting_morning
            in 12..17 -> R.string.greeting_afternoon
            in 18..23 -> R.string.greeting_evening
            else -> R.string.greeting_night
        }
    }
    
    val insightRes = remember(nextDrawAccumulated, isDrawDay) {
        when {
            nextDrawAccumulated -> R.string.greeting_insight_accumulated
            isDrawDay -> R.string.today_draw_active
            else -> R.string.greeting_insight_generic
        }
    }

    AnimateOnEntry(
        delayMillis = AppAnimationConstants.Delays.Minimal.toLong()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            // Greeting Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    Text(
                        text = "${stringResource(greetingRes)} $userName",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Insight/Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Icon(
                    imageVector = if (nextDrawAccumulated) Icons.Default.Star else Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = if (nextDrawAccumulated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = stringResource(insightRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Last update time
            if (lastUpdateTime != null) {
                Text(
                    text = stringResource(R.string.last_update_status, lastUpdateTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

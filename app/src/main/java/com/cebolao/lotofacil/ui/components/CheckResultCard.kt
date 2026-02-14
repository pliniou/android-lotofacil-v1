package com.cebolao.lotofacil.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun CheckResultCard(
    result: CheckResult,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        elevation = AppCardDefaults.elevation
    ) {
        Column(
            modifier = Modifier.padding(AppCardDefaults.defaultPadding),
            verticalArrangement = Arrangement.spacedBy(AppCardDefaults.contentSpacing)
        ) {
            val totalWins = result.scoreCounts.values.sum()
            val lastContestHit = result.recentHits.maxByOrNull { it.first }
            ResultHeader(totalWins, result.lastCheckedContest)
            lastContestHit?.let { (contest, hits) ->
                LatestContestHighlight(contest = contest, hits = hits)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            if (totalWins > 0) {
                ScoreBreakdown(result.scoreCounts)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                LastHitInfo(result)
            } else {
                NoWinsMessage()
            }
        }
    }
}

@Composable
private fun LatestContestHighlight(contest: Int, hits: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Icon(
            imageVector = Icons.Default.Celebration,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = pluralStringResource(
                id = R.plurals.checker_last_contest_highlight,
                count = hits,
                hits,
                contest
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResultHeader(totalWins: Int, contestsChecked: Int) {
    val icon = if (totalWins > 0) Icons.Default.Celebration else Icons.Default.Analytics
    val color = if (totalWins > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Column {
            Text(
                text = if (totalWins > 0) {
                    stringResource(id = R.string.awards_occurred)
                } else {
                    stringResource(id = R.string.performance_analysis)
                },
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Text(
                text = pluralStringResource(id = R.plurals.analysis_in_contests, contestsChecked, contestsChecked),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoreBreakdown(scoreCounts: ImmutableMap<Int, Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        for (score in 15 downTo 11) {
            scoreCounts[score]?.let { count ->
                val animated by animateIntAsState(
                    targetValue = count,
                    animationSpec = tween(AppTheme.motion.durationScoreCountMs),
                    label = "scoreCount"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        pluralStringResource(id = R.plurals.hits_count_label, score, score),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        pluralStringResource(id = R.plurals.times_count, animated, animated),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun LastHitInfo(result: CheckResult) {
    val lastHitContest = result.lastHitContest
    val lastHitScore = result.lastHitScore
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = if (lastHitContest != null && lastHitScore != null) {
                pluralStringResource(
                    id = R.plurals.last_award,
                    count = lastHitScore,
                    lastHitContest,
                    lastHitScore
                )
            } else {
                stringResource(id = R.string.no_awards_found)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NoWinsMessage() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(AppSpacing.sm))
        Text(
            stringResource(id = R.string.no_awards_found),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

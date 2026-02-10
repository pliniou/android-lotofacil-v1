package com.cebolao.lotofacil.ui.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormattedText(
    text: String,
    modifier: Modifier = Modifier,
    style: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
) {
    val annotatedString = remember(text, style) {
        buildAnnotatedString {
            val regex = "(<b>|</b>|<i>|</i>)".toRegex()
            var lastIndex = 0
            var bold = 0
            var italic = 0

            regex.findAll(text).forEach { match ->
                val startIndex = match.range.first
                if (startIndex > lastIndex) {
                    append(text.substring(lastIndex, startIndex))
                }
                when (match.value) {
                    "<b>" -> bold++
                    "</b>" -> bold--
                    "<i>" -> italic++
                    "</i>" -> italic--
                }

                val currentStyle = SpanStyle(
                    fontWeight = if (bold > 0) style.fontWeight else null,
                    color = if (bold > 0) style.color else Color.Unspecified,
                    fontStyle = if (italic > 0) FontStyle.Italic else null
                )
                withStyle(currentStyle) {
                    // Empty span for styling
                }
                lastIndex = match.range.last + 1
            }
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
fun InfoListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    description: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconContentDescription: String? = null
) {
    Row(
        verticalAlignment = if (description == null) Alignment.CenterVertically else Alignment.Top,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = iconTint,
            modifier = Modifier.padding(top = if (description == null) 0.dp else 4.dp)
        )
        Column {
            FormattedText(text = text)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

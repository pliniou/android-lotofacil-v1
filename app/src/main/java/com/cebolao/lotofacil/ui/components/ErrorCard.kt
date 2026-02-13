package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.ui.theme.AppCardDefaults
import com.cebolao.lotofacil.ui.testtags.AppTestTags

@Composable
fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag(AppTestTags.ErrorState),
        containerColor = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier
                .padding(AppCardDefaults.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBadge(
                icon = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                size = 40.dp,
                iconSize = 20.dp,
                tint = colors.error
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            actions?.invoke()
        }
    }
}

@Composable
fun ErrorCard(
    messageResId: Int,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null
) {
    ErrorCard(
        message = stringResource(id = messageResId),
        modifier = modifier,
        actions = actions
    )
}

@Composable
fun ErrorActions(
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    retryText: String = "",
    dismissText: String = ""
) {
    val resolvedRetryText = retryText.ifBlank {
        stringResource(id = R.string.try_again)
    }
    val resolvedDismissText = dismissText.ifBlank {
        stringResource(id = R.string.close_button)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        onDismiss?.let { dismiss ->
            OutlinedButton(onClick = dismiss) {
                Text(resolvedDismissText)
            }
        }
        onRetry?.let { retry ->
            Button(onClick = retry) {
                Text(resolvedRetryText)
            }
        }
    }
}

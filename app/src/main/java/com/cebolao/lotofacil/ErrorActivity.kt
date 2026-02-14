package com.cebolao.lotofacil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cebolao.lotofacil.ui.components.AppCard
import com.cebolao.lotofacil.ui.theme.AppSpacing
import com.cebolao.lotofacil.ui.theme.LotofacilTheme
import java.util.concurrent.atomic.AtomicBoolean

class ErrorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashMessage = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
        val stackTrace = intent.getStringExtra(EXTRA_STACKTRACE).orEmpty()

        setContent {
            LotofacilTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ErrorScreenContent(
                        crashMessage = crashMessage,
                        stackTrace = stackTrace,
                        onRestart = { restartApp() },
                        onReport = { reportCrash(crashMessage, stackTrace) }
                    )
                }
            }
        }
    }

    private fun restartApp() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun reportCrash(message: String, stackTrace: String) {
        val report = buildString {
            appendLine("App: ${BuildConfig.APPLICATION_ID}")
            appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Android: ${android.os.Build.VERSION.RELEASE}")
            appendLine("Message: ${message.ifBlank { "Unknown error" }}")
            if (stackTrace.isNotBlank()) {
                appendLine("Stacktrace:")
                appendLine(stackTrace)
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.global_error_report_subject))
            putExtra(Intent.EXTRA_TEXT, report)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.global_error_report)))
    }

    companion object {
        private const val EXTRA_MESSAGE = "extra_message"
        private const val EXTRA_STACKTRACE = "extra_stacktrace"
        private val isHandlingCrash = AtomicBoolean(false)

        fun launch(context: Context, throwable: Throwable) {
            if (!isHandlingCrash.compareAndSet(false, true)) return
            val intent = Intent(context, ErrorActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(EXTRA_MESSAGE, throwable.message ?: throwable.javaClass.simpleName)
                putExtra(EXTRA_STACKTRACE, throwable.stackTraceToString().take(10_000))
            }
            context.startActivity(intent)
        }
    }
}

@Composable
private fun ErrorScreenContent(
    crashMessage: String,
    stackTrace: String,
    onRestart: () -> Unit,
    onReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppCard(
            modifier = Modifier.widthIn(max = 520.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(id = R.string.global_error_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.global_error_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                if (crashMessage.isNotBlank()) {
                    Text(
                        text = crashMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(AppSpacing.sm))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Button(onClick = onRestart) {
                        Text(text = stringResource(id = R.string.global_error_restart))
                    }
                    OutlinedButton(onClick = onReport) {
                        Text(text = stringResource(id = R.string.global_error_report))
                    }
                }
            }
        }

        if (BuildConfig.DEBUG && stackTrace.isNotBlank()) {
            Spacer(modifier = Modifier.size(AppSpacing.lg))
            Text(
                text = stackTrace,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
    }
}

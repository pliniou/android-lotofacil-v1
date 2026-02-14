package com.cebolao.lotofacil

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.ui.components.AppScreenStateHost
import com.cebolao.lotofacil.ui.components.ScreenContentState
import com.cebolao.lotofacil.ui.screens.MainScreen
import com.cebolao.lotofacil.domain.model.ThemeMode
import com.cebolao.lotofacil.ui.theme.LotofacilTheme
import com.cebolao.lotofacil.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            val state = mainViewModel.uiState.value
            state.isLoading && !state.hasError
        }

        // Default system splash screen animation will be used


        setContent {
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (uiState.themeMode) {
                ThemeMode.SYSTEM -> systemDarkTheme
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            LotofacilTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startupScreenState = remember(uiState.isReady, uiState.hasError, uiState.errorMessageResId) {
                        when {
                            uiState.isReady -> ScreenContentState.Success
                            uiState.hasError -> ScreenContentState.Error(
                                messageResId = uiState.errorMessageResId ?: R.string.error_load_data_failed
                            )

                            else -> ScreenContentState.Loading()
                        }
                    }

                    AppScreenStateHost(
                        state = startupScreenState,
                        modifier = Modifier.fillMaxSize(),
                        onRetry = { mainViewModel.retryInitialization() }
                    ) {
                        MainScreen(
                            themeMode = uiState.themeMode,
                            onThemeModeSelected = mainViewModel::setThemeMode
                        )
                    }
                }
            }
        }
    }
}

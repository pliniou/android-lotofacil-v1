package com.cebolao.lotofacil

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cebolao.lotofacil.ui.screens.MainScreen
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import com.cebolao.lotofacil.ui.theme.DefaultAppMotion
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
            mainViewModel.uiState.value.isReady.not()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.view.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = DefaultAppMotion.durationLongMs.toLong()

                val fadeOut = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.ALPHA,
                    1f,
                    0f
                )
                fadeOut.duration = DefaultAppMotion.durationLongMs.toLong()

                AnimatorSet().apply {
                    playTogether(slideUp, fadeOut)
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }

        setContent {
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            CebolaoLotofacilTheme(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (uiState.isReady) {
                        val navController = androidx.navigation.compose.rememberNavController()
                        MainScreen(navController = navController)
                    }
                }
            }
        }
    }
}

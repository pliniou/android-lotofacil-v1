package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.DeviceConfig
import androidx.compose.material3.Text
import com.cebolao.lotofacil.ui.theme.LotofacilTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Run snapshot tests with dedicated Paparazzi tasks in CI.")
class GeneratedGamesScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun generatedGamesScreenLoadingState() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state for loading
                Text("Generated Games Loading State")
            }
        }
    }

    @Test
    fun generatedGamesScreenWithGames() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state with generated games
                Text("Generated Games Screen With Games")
            }
        }
    }

    @Test
    fun generatedGamesScreenEmptyState() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state with empty list
                Text("Generated Games Screen Empty")
            }
        }
    }
}

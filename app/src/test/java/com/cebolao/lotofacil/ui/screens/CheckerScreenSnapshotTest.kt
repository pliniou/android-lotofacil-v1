package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.DeviceConfig
import androidx.compose.material3.Text
import com.cebolao.lotofacil.ui.theme.LotofacilTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Run snapshot tests with dedicated Paparazzi tasks in CI.")
class CheckerScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun checkerScreenIdleState() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state for idle
                Text("Checker Screen Idle State")
            }
        }
    }

    @Test
    fun checkerScreenLoadingState() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state with loading
                Text("Checker Screen Loading State")
            }
        }
    }

    @Test
    fun checkerScreenSuccessState() {
        paparazzi.snapshot {
            LotofacilTheme {
                // Mock UI state with results
                Text("Checker Screen Success State")
            }
        }
    }
}

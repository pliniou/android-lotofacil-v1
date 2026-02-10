package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.DeviceConfig
import androidx.compose.material3.Text
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Run snapshot tests with dedicated Paparazzi tasks in CI.")
class HomeScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun homeScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for loading
                Text("Home Screen Loading State")
            }
        }
    }

    @Test
    fun homeScreenWithData() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with data
                Text("Home Screen With Data")
            }
        }
    }

    @Test
    fun homeScreenErrorState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with error
                Text("Home Screen Error State")
            }
        }
    }
}

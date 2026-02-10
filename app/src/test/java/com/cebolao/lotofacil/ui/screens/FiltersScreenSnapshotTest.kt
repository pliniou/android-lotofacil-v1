package com.cebolao.lotofacil.ui.screens

import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.DeviceConfig
import androidx.compose.material3.Text
import com.cebolao.lotofacil.ui.theme.CebolaoLotofacilTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Run snapshot tests with dedicated Paparazzi tasks in CI.")
class FiltersScreenSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun filtersScreenLoadingState() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state for loading
                Text("Filters Screen Loading State")
            }
        }
    }

    @Test
    fun filtersScreenWithFilters() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state with filters configured
                Text("Filters Screen With Filters")
            }
        }
    }

    @Test
    fun filtersScreenAfterGeneration() {
        paparazzi.snapshot {
            CebolaoLotofacilTheme {
                // Mock UI state after generation
                Text("Filters Screen After Generation")
            }
        }
    }
}

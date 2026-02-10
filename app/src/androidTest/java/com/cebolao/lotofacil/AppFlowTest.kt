package com.cebolao.lotofacil

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun homeRefreshAction_isAvailable() {
        navigateTo("home")

        composeTestRule.onNodeWithTag(AppTestTags.HomeRefreshAction)
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
    }

    @Test
    fun filtersGenerate_navigatesToGeneratedGames() {
        navigateTo("filters")

        composeTestRule.onNodeWithTag(AppTestTags.FiltersGenerateButton)
            .performScrollTo()
            .performClick()

        waitForBottomNavSelected("games")
        composeTestRule.onNodeWithTag(bottomNavTag("games")).assertIsSelected()
    }

    @Test
    fun insightsGaussianToggle_isInteractive() {
        navigateTo("home")

        composeTestRule.onNodeWithTag(AppTestTags.HomeInsightsButton)
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag(AppTestTags.InsightsGaussianToggle).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag(AppTestTags.InsightsGaussianToggle)
            .assertIsDisplayed()
            .performClick()
    }

    private fun navigateTo(route: String) {
        composeTestRule.onNodeWithTag(bottomNavTag(route)).performClick()
    }

    private fun waitForBottomNavSelected(route: String) {
        val tag = bottomNavTag(route)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag(tag)
                .fetchSemanticsNodes()
                .any { node -> node.config.getOrNull(SemanticsProperties.Selected) == true }
        }
    }

    private fun bottomNavTag(route: String) = "${AppTestTags.BottomNavItemPrefix}$route"
}

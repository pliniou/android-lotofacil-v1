package com.cebolao.lotofacil

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.swipeDown
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
            .performTouchInput { swipeDown() }

        composeTestRule.waitForIdle()
    }

    @Test
    fun filtersGenerate_navigatesToGeneratedGames() {
        navigateTo("filters")

        if (composeTestRule.hasNodesWithTag(AppTestTags.FiltersGenerateButton)) {
            composeTestRule.onNodeWithTag(AppTestTags.FiltersGenerateButton)
                .performScrollTo()
                .performClick()

            waitForBottomNavSelected("games")
            composeTestRule.onNodeWithTag(bottomNavTag("games")).assertIsSelected()
        }
    }

    @Test
    fun insightsGaussianToggle_isInteractive() {
        navigateTo("home")

        if (composeTestRule.hasNodesWithTag(AppTestTags.HomeInsightsButton)) {
            composeTestRule.onNodeWithTag(AppTestTags.HomeInsightsButton)
                .performScrollTo()
                .performClick()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.hasNodesWithTag(AppTestTags.InsightsGaussianToggle) ||
                    composeTestRule.hasNodesWithTag(AppTestTags.EmptyState) ||
                    composeTestRule.hasNodesWithTag(AppTestTags.ErrorState)
            }

            if (composeTestRule.hasNodesWithTag(AppTestTags.InsightsGaussianToggle)) {
                composeTestRule.onNodeWithTag(AppTestTags.InsightsGaussianToggle)
                    .assertIsDisplayed()
                    .performClick()
            } else if (composeTestRule.hasNodesWithTag(AppTestTags.EmptyState)) {
                composeTestRule.onNodeWithTag(AppTestTags.EmptyState).assertIsDisplayed()
            } else if (composeTestRule.hasNodesWithTag(AppTestTags.ErrorState)) {
                composeTestRule.onNodeWithTag(AppTestTags.ErrorState).assertIsDisplayed()
            }
        } else if (composeTestRule.hasNodesWithTag(AppTestTags.EmptyState)) {
            composeTestRule.onNodeWithTag(AppTestTags.EmptyState).assertIsDisplayed()
        } else if (composeTestRule.hasNodesWithTag(AppTestTags.ErrorState)) {
            composeTestRule.onNodeWithTag(AppTestTags.ErrorState).assertIsDisplayed()
        }
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

    private fun androidx.compose.ui.test.junit4.AndroidComposeTestRule<*, *>.hasNodesWithTag(tag: String): Boolean {
        return onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
}

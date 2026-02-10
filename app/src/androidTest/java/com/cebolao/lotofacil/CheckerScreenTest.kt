package com.cebolao.lotofacil

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cebolao.lotofacil.domain.model.LotofacilConstants
import com.cebolao.lotofacil.ui.testtags.AppTestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CheckerScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        val checkerTag = "${AppTestTags.BottomNavItemPrefix}checker"
        composeTestRule.onNodeWithTag(checkerTag).performClick()
    }

    @Test
    fun checkerScreen_when15NumbersAreSelected_checkButtonIsEnabled() {
        composeTestRule.onNodeWithTag(AppTestTags.CheckerCheckButton).assertIsNotEnabled()

        for (i in 1..LotofacilConstants.GAME_SIZE) {
            composeTestRule.onNodeWithTag("${AppTestTags.NumberBallPrefix}$i").performClick()
        }

        composeTestRule.onNodeWithTag(AppTestTags.CheckerCheckButton)
            .performScrollTo()
            .assertIsEnabled()
    }

    @Test
    fun checkerScreen_whenCheckButtonIsClicked_resultIsShown() {
        for (i in 1..LotofacilConstants.GAME_SIZE) {
            composeTestRule.onNodeWithTag("${AppTestTags.NumberBallPrefix}$i").performClick()
        }

        composeTestRule.onNodeWithTag(AppTestTags.CheckerCheckButton).performScrollTo().performClick()

        val activity = composeTestRule.activity
        val performanceTitle = activity.getString(R.string.performance_analysis)
        val statsTitle = activity.getString(R.string.game_stats_title)

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(performanceTitle).fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodesWithText(statsTitle).fetchSemanticsNodes().isNotEmpty()
        }

        try {
            composeTestRule.onNodeWithText(performanceTitle).assertIsDisplayed()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText(statsTitle).assertIsDisplayed()
        }
    }

    @Test
    fun checkerScreen_clearButtonResetsSelection() {
        composeTestRule.onNodeWithTag("${AppTestTags.NumberBallPrefix}1").performClick()
        composeTestRule.onNodeWithTag("${AppTestTags.NumberBallPrefix}2").performClick()
        composeTestRule.onNodeWithTag("${AppTestTags.NumberBallPrefix}3").performClick()

        composeTestRule.onNodeWithTag(AppTestTags.CheckerClearButton)
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithTag(AppTestTags.ConfirmationDialogConfirm).performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag(AppTestTags.CheckerClearButton).fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithTag(AppTestTags.CheckerCheckButton).assertIsNotEnabled()
    }
}

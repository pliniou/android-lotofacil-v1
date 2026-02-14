package com.cebolao.lotofacil.ui.text

import androidx.annotation.StringRes
import com.cebolao.lotofacil.R

/**
 * Centralized semantic mapping for user-facing copy.
 * Text values remain in Android string resources for localization.
 */
object AppStrings {

    object Greetings {
        @StringRes val helloName = R.string.greeting_hello_name
        @StringRes val defaultUser = R.string.greeting_user
        @StringRes val insightAccumulated = R.string.greeting_insight_accumulated
        @StringRes val insightDefault = R.string.greeting_insight_generic
        @StringRes val todayContest = R.string.today_draw_active
    }

    object Ctas {
        @StringRes val generateGame = R.string.cta_generate_game
        @StringRes val generateNewGame = R.string.generate_new_game_button
        @StringRes val generateFirstGame = R.string.generate_first_game
        @StringRes val viewAll = R.string.common_view_all
    }

    object Labels {
        @StringRes val nextContestTitle = R.string.next_draw_card_title
        @StringRes val selectedNumbers = R.string.selected_numbers_counter
        @StringRes val analysisType = R.string.home_analysis_type_label
        @StringRes val period = R.string.home_period_label
    }

    object Errors {
        @StringRes val loadData = R.string.error_load_data_failed
        @StringRes val generation = R.string.error_generating_games
        @StringRes val unknown = R.string.error_unknown
        @StringRes val checker = R.string.checker_checking_error
    }

    object EmptyStates {
        @StringRes val home = R.string.home_empty_message
        @StringRes val games = R.string.empty_games_title
        @StringRes val insights = R.string.insights_empty_message
    }

    object Tooltips {
        @StringRes val analysisType = R.string.tooltip_analysis_type
        @StringRes val period = R.string.tooltip_period
    }
}

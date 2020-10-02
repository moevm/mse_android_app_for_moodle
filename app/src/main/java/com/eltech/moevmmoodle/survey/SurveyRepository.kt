package com.eltech.moevmmoodle.survey

import com.eltech.moevmmoodle.R
import com.eltech.moevmmoodle.survey.PossibleAnswer.Action
import com.eltech.moevmmoodle.survey.PossibleAnswer.MultipleChoice
import com.eltech.moevmmoodle.survey.PossibleAnswer.SingleChoice
import com.eltech.moevmmoodle.survey.SurveyActionType.PICK_DATE

// Static data of questions
private val jetpackQuestions = listOf(
    Question(
        id = 1,
        questionText = R.string.in_my_free_time,
        answer = MultipleChoice(
            optionsStringRes = listOf(
                R.string.read,
                R.string.work_out,
                R.string.draw,
                R.string.play_games,
                R.string.dance,
                R.string.watch_movies
            )
        )
    ),
    Question(
        id = 2,
        questionText = R.string.pick_superhero,
        answer = SingleChoice(
            optionsStringRes = listOf(
                R.string.spiderman,
                R.string.ironman,
                R.string.unikitty,
                R.string.captain_planet
            )
        )
    ),
    Question(
        id = 7,
        questionText = R.string.favourite_movie,
        answer = SingleChoice(
            listOf(
                R.string.star_trek,
                R.string.social_network,
                R.string.back_to_future,
                R.string.outbreak
            )
        )
    ),
    Question(
        id = 3,
        questionText = R.string.takeaway,
        answer = Action(label = R.string.pick_date, actionType = PICK_DATE)
    ),
    Question(
        id = 4,
        questionText = R.string.selfies,
        answer = PossibleAnswer.Slider(
            range = 1f..10f,
            steps = 3,
            startText = R.string.selfie_min,
            endText = R.string.selfie_max
        )
    )
)
private val jetpackSurvey = Survey(
    title = R.string.which_jetpack_library,
    questions = jetpackQuestions
)

object SurveyRepository {

    suspend fun getSurvey() = jetpackSurvey

    fun getSurveyResult(answers: List<Answer<*>>): SurveyResult {
        return SurveyResult(
            library = "Compose",
            result = R.string.survey_result,
            description = R.string.survey_result_description
        )
    }
}

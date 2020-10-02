package com.eltech.moevmmoodle.survey

import android.graphics.Bitmap
import androidx.annotation.StringRes

data class SurveyResult(
    val library: String,
    @StringRes val result: Int,
    @StringRes val description: Int
)

data class Survey(
    @StringRes val title: Int,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    @StringRes val questionText: Int,
    val answer: PossibleAnswer
)

/**
 * Type of supported actions for a survey
 */
enum class SurveyActionType { PICK_DATE, TAKE_PHOTO, SELECT_CONTACT }

sealed class SurveyActionResult {
    data class Date(val date: String) : SurveyActionResult()
    data class Photo(val bitmap: Bitmap) : SurveyActionResult()
    data class Contact(val contact: String) : SurveyActionResult()
}

sealed class PossibleAnswer {
    data class SingleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    data class MultipleChoice(val optionsStringRes: List<Int>) : PossibleAnswer()
    data class Action(
        @StringRes val label: Int,
        val actionType: SurveyActionType
    ) : PossibleAnswer()

    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val steps: Int,
        @StringRes val startText: Int,
        @StringRes val endText: Int,
        val defaultValue: Float = range.start
    ) : PossibleAnswer()
}

sealed class Answer<T : PossibleAnswer> {
    data class SingleChoice(@StringRes val answer: Int) : Answer<PossibleAnswer.SingleChoice>()
    data class MultipleChoice(val answersStringRes: Set<Int>) :
        Answer<PossibleAnswer.MultipleChoice>()

    data class Action(val result: SurveyActionResult) : Answer<PossibleAnswer.Action>()
    data class Slider(val answerValue: Float) : Answer<PossibleAnswer.Slider>()
}

/**
 * Add or remove an answer from the list of selected answers depending on whether the answer was
 * selected or deselected.
 */
fun Answer.MultipleChoice.withAnswerSelected(
    @StringRes answer: Int,
    selected: Boolean
): Answer.MultipleChoice {
    val newStringRes = answersStringRes.toMutableSet()
    if (!selected) {
        newStringRes.remove(answer)
    } else {
        newStringRes.add(answer)
    }
    return Answer.MultipleChoice(newStringRes)
}

package info.moevm.moodle.data.courses

import info.moevm.moodle.model.File

data class ResponseFileAreas(
    val area: String? = null,
    val files: List<File>? = null
)

/**
slot: Int?,                                                           slot number
type: String?,                                                        question type, i.e: multichoice
page: Int?,                                                           page of the quiz this question appears on
html: String?,                                                        the question rendered
responsefileareas: List<ResponseFileAreas>?             Необязательно Response file areas including files
sequencecheck: Int?,                                    Необязательно the number of real steps in this attempt
lastactiontime: Int?,                                   Необязательно the timestamp of the most recent step in this question attempt
hasautosavedstep: Boolean?,                             Необязательно whether this question attempt has autosaved data
flagged: Boolean?,                                                    whether the question is flagged or not
number: Int?,                                           Необязательно question ordering number in the quiz
state: String?,                                         Необязательно the state where the question is in. It will not be returned if the user cannot see it due to the quiz display correctness settings.
status: String?,                                        Необязательно current formatted state of the question
blockedbyprevious: Boolean?,                            Необязательно whether the question is blocked by the previous question
mark: String?,                                          Необязательно the mark awarded. It will be returned only if the user is allowed to see it.
maxmark: Int,                                           Необязательно the maximum mark possible for this question attempt. It will be returned only if the user is allowed to see it.
settings: String?                                       Необязательно Question settings (JSON encoded).
 */

data class Question(
    val slot: Int? = null,
    val type: String? = null,
    val page: Int? = null,
    val html: String? = null,
    val responsefileareas: List<ResponseFileAreas>? = null,
    val sequencecheck: Int? = null,
    val lastactiontime: Int? = null,
    val hasautosavedstep: Boolean? = null,
    val flagged: Boolean? = null,
    val number: Int? = null,
    val state: String? = null,
    val status: String? = null,
    val blockedbyprevious: Boolean? = null,
    val mark: String? = null,
    val maxmark: Int? = null,
    val settings: String? = null
)

/**
messages: List<String>?                                               access messages, will only be returned for users with mod/quiz:preview capability, for other users this method will throw an exception if there are messages
nextpage: Int?                                                        next page number
 */

data class QuizInProgress(
    val attempt: Attempt? = null,
    val messages: List<String>? = null,
    val nextpage: Int? = null,
    val questions: List<Question>? = null,
    val warnings: List<WarningItem>? = null
)

/**
id: String?                                     id of the data
title: String?                                  data title
content: String?                                data content
 */

data class AdditionalData(
    val id: String? = null,
    val title: String? = null,
    val content: String? = null,
)

data class QuizFinished(
    val grade: Int? = null,
    val attempt: Attempt? = null,
    val additionaldata: List<AdditionalData>? = null,
    val questions: List<Question>? = null,
    val warnings: List<WarningItem>? = null
)

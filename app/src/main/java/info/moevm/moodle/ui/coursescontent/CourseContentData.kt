package info.moevm.moodle.ui.coursescontent

import androidx.compose.runtime.Composable
import info.moevm.moodle.R
import java.util.*

enum class TaskType(val value: String = "") { NONE(""), LESSON("lesson"), QUIZ("quiz"), FORUM("forum"), LTI("lti") }
enum class TaskStatus(val value: Int = -1) { NONE(-1), WORKING(0), DONE(1), FAILED(2), RELOAD(3) } // TODO: исправить на нужные из документации
enum class AttemptStatus(val value: String) { IN_PROGRESS("inprogress"), OVERDUE("overdue"), FINISHED("finished"), ABANDONED("abandoned") }
enum class TestStepType(val value: String) { MULTICHOICE("multichoice"), SHORTANSWER("shortanswer"), NUMERICAL("numerical"), MATCH("match") }
enum class TaskContentType { ARTICLE, TEST_IN_PROGRESS, TEST_FINISHED, UNSUPPORTED }
enum class TaskAnswerType { NONE, NUMBERS, TEXT }

open class TaskContentItem(
    open val taskTitle: String,
    open val taskContentType: TaskContentType,
    open val taskMark: String,
    open val taskContent: @Composable () -> Unit
)

data class ArticleTaskContentItem(
    override val taskTitle: String,
    override val taskContentType: TaskContentType,
    override val taskMark: String,
    override val taskContent: @Composable () -> Unit
) : TaskContentItem(
    taskTitle, taskContentType, taskMark, taskContent
)

data class QuizTaskContentItem(
    override val taskTitle: String,
    override val taskContentType: TaskContentType,
    override val taskMark: String,
    override val taskContent: @Composable () -> Unit
) : TaskContentItem(
    taskTitle, taskContentType, taskMark, taskContent
)

fun getTaskStatusIconId(taskStatus: TaskStatus): Int {
    return when (taskStatus) {
        TaskStatus.WORKING -> R.drawable.test_working
        TaskStatus.DONE -> R.drawable.test_done
        TaskStatus.RELOAD -> R.drawable.refresh_icon
        else -> R.drawable.empty_img
    }
}

fun getTaskTypeIconId(taskType: TaskType): Int {
    return when (taskType) {
        TaskType.LESSON -> R.drawable.topic_logo
        TaskType.QUIZ -> R.drawable.test_logo
        else -> R.drawable.empty_img
    }
}

package info.moevm.moodle.ui.coursescontent

import androidx.compose.runtime.Composable
import info.moevm.moodle.R
import java.util.*

typealias CourseMapData = Map<String, List<CourseContentItem>>

enum class TaskType(val value: String = "") { NONE(""), LESSON("lesson"), QUIZ("quiz"), FORUM("forum"), LTI("lti") }
enum class TaskStatus(val value: Int = -1) { NONE(-1), WORKING(0), DONE(1), FAILED(2), RELOAD(3) } // TODO: исправить на нужные из документации
enum class TaskContentType { VIDEO, ARTICLE, TEST_ONE_CHOICE, TEST_MULTI_CHOICE, TEST_ANSWER, TEST_MATCH, UNSUPPORTED }
enum class TaskAnswerType { NONE, NUMBERS, TEXT }

data class CourseContentItem(
    val lessonTitle: String,
    val lessonContent: List<LessonContentItem?>
)

open class LessonContentItem(
    open val taskType: TaskType,
    open val taskTitle: String,
    open val taskStatus: TaskStatus
) // taskContent можно использовать для окон с темами и тестами курса

data class ArticleContentItems(
    override val taskType: TaskType,
    override val taskTitle: String,
    override val taskStatus: TaskStatus,
    val taskContent: List<TaskContentItem?> = listOf()
) : LessonContentItem(
    taskType, taskTitle, taskStatus
)

data class AttemptData(
    val id: Int,
    val date: String,
    val taskStatus: TaskStatus
)

data class TestContentItems(
    override val taskType: TaskType,
    override val taskTitle: String,
    override val taskStatus: TaskStatus,
    val taskMark: String,
    val taskContent: HashMap<String, Pair<AttemptData, List<TaskContentItem?>>>
) : LessonContentItem(
    taskType, taskTitle, taskStatus
)

open class TaskContentItem(
    open val taskTitle: String, // Может использоваться как нумерация вопроса
    open val taskContentType: TaskContentType,
    open val taskMark: String,
    open val taskContentStatus: TaskStatus,
    open val taskContent: @Composable () -> Unit
)

data class ArticleTaskContentItem(
    override val taskTitle: String, // Может использоваться как нумерация вопроса
    override val taskContentType: TaskContentType,
    override val taskMark: String,
    override val taskContentStatus: TaskStatus,
    override val taskContent: @Composable () -> Unit
) : TaskContentItem(
    taskTitle, taskContentType, taskMark, taskContentStatus, taskContent
)

data class TestTaskContentItem(
    override val taskTitle: String, // Может использоваться как нумерация вопроса
    override val taskContentType: TaskContentType,
    override val taskMark: String,
    override val taskContentStatus: TaskStatus,
    val taskAnswers: List<String> = listOf(),
    val taskAnswerType: TaskAnswerType = TaskAnswerType.TEXT,
    val taskRightAnswers: List<String> = listOf(),
    val taskAdditionInfo: List<String> = listOf(),
    override val taskContent: @Composable () -> Unit,
) : TaskContentItem(
    taskTitle, taskContentType, taskMark, taskContentStatus, taskContent
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

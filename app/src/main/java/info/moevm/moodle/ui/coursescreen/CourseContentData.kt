package info.moevm.moodle.ui.coursescreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import info.moevm.moodle.R
import java.util.*

typealias CourseMapData = Map<String, List<CourseContentItem>>

enum class TaskType { NONE, TOPIC, TEST }
enum class TaskStatus { NONE, WORKING, DONE, FAILED, RELOAD }
enum class TaskContentType { VIDEO, ARTICLE, TEST_ONE_CHOICE, TEST_MULTI_CHOICE, TEST_ANSWER, TEST_MATCH, UNSUPPORTED }

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
    val taskContent: HashMap<String, Pair<AttemptData,List<TaskContentItem?>>>
): LessonContentItem(
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
        TaskType.TOPIC -> R.drawable.topic_logo
        TaskType.TEST -> R.drawable.test_logo
        TaskType.NONE -> R.drawable.empty_img
    }
}

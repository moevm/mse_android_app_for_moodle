package info.moevm.moodle.ui.coursescreen

import androidx.compose.runtime.Composable
import info.moevm.moodle.R

typealias CourseMapData = Map<String, List<CourseContentItem>>

enum class TaskType { TOPIC, TEST }
enum class TaskStatus { NONE, WORKING, DONE }
enum class TaskContentStatus { FAILED, ACCEPTED }
enum class TaskContentType { VIDEO, ARTICLE, TEST_ONE_CHOICE, TEST_MULTI_CHOICE, TEST_ANSWER, TEST_MATCH, UNSUPPORTED }


data class CourseContentItem(val lessonTitle: String, val lessonContent: List<LessonContentItem>)
data class LessonContentItem(val taskType: TaskType, val taskTitle: String, val taskStatus: TaskStatus, val taskContent: List<TaskContentItem> = listOf())//taskContent можно использовать для окон с темами и тестами курса

data class TaskContentItem(
    val taskTitle: String, //Может использоваться как нумерация вопроса
    val taskContentType: TaskContentType,
    val taskMark: String,
    val taskContentStatus: TaskContentStatus,
    val taskContent: @Composable () -> Unit
)

fun getTaskStatusIconId(taskStatus: TaskStatus): Int {
    return when (taskStatus) {
        TaskStatus.WORKING -> R.drawable.test_working
        TaskStatus.DONE -> R.drawable.test_done
        TaskStatus.NONE -> R.drawable.empty_img
    }
}
fun getTaskTypeIconId(taskType: TaskType): Int {
    return when (taskType) {
        TaskType.TOPIC -> R.drawable.topic_logo
        TaskType.TEST -> R.drawable.test_logo
    }
}

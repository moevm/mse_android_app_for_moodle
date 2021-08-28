package info.moevm.moodle.data.courses

import info.moevm.moodle.ui.coursescreen.TaskStatus
import info.moevm.moodle.ui.coursescreen.TaskType

typealias CourseMapData = Map<String, List<CourseContentItem>>

data class CourseContentItem(val lessonTitle: String, val lessonContent: List<LessonContentItem>)
data class LessonContentItem(val taskType: TaskType, val taskTitle: String, val taskStatus: TaskStatus)

fun exampleCourseContent() : CourseMapData{
    return mapOf(
        "Курс молодого бойца" to listOf(
            CourseContentItem("Общие правила работы проектами",
                listOf(
                    LessonContentItem(TaskType.TOPIC, "Введение", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Автобусный фактор (bus factor)", TaskStatus.NONE),
                    LessonContentItem(TaskType.TEST, "Общие правила работы проектами. Тест", TaskStatus.WORKING)
                )
            ),
            CourseContentItem("Планирование календаря",
                listOf(
                    LessonContentItem(TaskType.TOPIC, "Как планировать календарь работы", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Планирование и оценка времени выполнения задач", TaskStatus.NONE),
                    LessonContentItem(TaskType.TEST, "Планирование календаря. Тест", TaskStatus.NONE)
                )
            ),
            CourseContentItem("Коммуникация по проекту",
                listOf(
                    LessonContentItem(TaskType.TOPIC, "Решение оперативных задач", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Как писать письма", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Описание проблем в письмах", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Протоколы встреч", TaskStatus.NONE),
                    LessonContentItem(TaskType.TEST, "Коммуникация по проекту. Тест", TaskStatus.WORKING),
                )
            ),
            CourseContentItem("Работа с задачами",
                listOf(
                    LessonContentItem(TaskType.TOPIC, "Трекеры задач", TaskStatus.WORKING),
                    LessonContentItem(TaskType.TOPIC, "Наблюдаемые результаты задач", TaskStatus.NONE),
                    LessonContentItem(TaskType.TOPIC, "Декомпозиция задач", TaskStatus.NONE),
                    LessonContentItem(TaskType.TEST, "Работа с задачами. Тест", TaskStatus.DONE),
                    LessonContentItem(TaskType.TOPIC, "Прощание", TaskStatus.WORKING),
                )
            )
        )
    )
}

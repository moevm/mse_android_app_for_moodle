package info.moevm.moodle.data.courses

import androidx.compose.runtime.MutableState
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.model.CurrentCourses
import info.moevm.moodle.ui.coursescreen.*
import kotlinx.coroutines.*
import timber.log.Timber

class CourseManager(
    private var token: String,
    private val moodleApi: MoodleApi,
    private val courseId: MutableState<Int>, // id курса
    private val courseContentItemIndex: MutableState<Int>, // index категории курса
    private val lessonContentItemIndex: MutableState<Int>, // index урока в категории
    private val taskContentItemIndex: MutableState<Int>, // index занятия в уроке
    private val testAttemptKey: MutableState<String>, // index попытки
) {
    private var courseName: String = ""
    private var courseContentData: List<CourseMoodleContentData>? = null
    private var coursesLoaded: Boolean = false
    private var articleLessonContent: ArticleContentItems? = null
    private var testLessonContent: TestContentItems? = null

    var requiredMoveLessonIndexForward: Boolean = false
    var requiredMoveLessonIndexBack: Boolean = false

    private fun changeLessonItem() {
        if (courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.modname == TaskType.LESSON.value) {
            articleLessonContent = ArticleContentItems(
                TaskType.LESSON,
                courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.name ?: "",
                when (courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.completiondata?.state) {
                    TaskStatus.DONE.value -> TaskStatus.DONE
                    TaskStatus.WORKING.value -> TaskStatus.WORKING
                    else -> TaskStatus.NONE
                }
            )
        } else if (courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.modname == TaskType.QUIZ.value) {
            testLessonContent = TestContentItems(
                TaskType.QUIZ,
                courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.name ?: "",
                when (courseContentData?.get(courseContentItemIndex.value)?.modules?.get(lessonContentItemIndex.value)?.completiondata?.state) {
                    TaskStatus.DONE.value -> TaskStatus.DONE
                    TaskStatus.WORKING.value -> TaskStatus.WORKING
                    else -> TaskStatus.NONE
                },
                hashMapOf()
            )
        } else {} /// ?????????????????





//        val lessonContent =
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            )
//        val lessonContent =
//            courseContentData?.get(courseContentItemIndex.value)?.modules?.get(
//                lessonContentItemIndex.value
//            )
//        when (lessonContent?.modname) {
//            TaskType.TOPIC.value -> { // TODO: починить
////                articleLessonContent = lessonContent as ArticleContentItems
////                testLessonContent = null
//            }
//            TaskType.TEST.value -> {
////                articleLessonContent = null
////                testLessonContent = lessonContent as TestContentItems
//            }
//            else -> {
//            }
//        }
    }

    fun receiveCurrentCourseTitles(): CurrentCourses? {
        var data: CurrentCourses? = null
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (token != "") {
                    data = moodleApi.getCurrentCourses(token)
                }
            }
        }
        return data
    }

    fun receiveCourseContentData(/*token: String,*/ courseId: String) { // считывает содержимое одного курса
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                courseContentData = moodleApi.getCourseContent(token, courseId)
            }
        }
    }

    // Setters
    fun moveTaskIndex(delta: Int = 1) {
        if (articleLessonContent != null) {
            if (0 <= taskContentItemIndex.value + delta && taskContentItemIndex.value + delta < articleLessonContent?.taskContent?.size ?: 0)
                taskContentItemIndex.value += delta
        } else if (testLessonContent != null) {
            if (0 <= taskContentItemIndex.value + delta && taskContentItemIndex.value + delta < testLessonContent?.taskContent?.get(
                    testAttemptKey.value
                )?.second?.size ?: 0
            )
                taskContentItemIndex.value += delta
        }
        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setTaskIndex(newIndex: Int) {
        if (articleLessonContent != null) {
            if (0 <= newIndex && newIndex < articleLessonContent?.taskContent?.size ?: 0)
                taskContentItemIndex.value = newIndex
        } else if (testLessonContent != null) {
            if (0 <= newIndex && newIndex < testLessonContent?.taskContent?.get(
                    testAttemptKey.value
                )?.second?.size ?: 0
            )
                taskContentItemIndex.value = newIndex
        }
        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setAttemptKey(newTestAttemptKey: String) {
        if (testLessonContent != null && testLessonContent?.taskContent.orEmpty()
                .contains(newTestAttemptKey)
        ) {
            testAttemptKey.value = newTestAttemptKey
            taskContentItemIndex.value = 0
        }
        Timber.i("testAttemptKey: ${testAttemptKey.value}")
    }

    fun moveLessonIndex(delta: Int = 1) {
//        if (0 <= lessonContentItemIndex.value + delta &&
//            lessonContentItemIndex.value + delta < courseContentData?.get(
//                courseContentItemIndex.value
//            )?.lessonContent?.size ?: 0 &&
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value + delta
//            ) != null
//        ) {
//            lessonContentItemIndex.value += delta
//            changeLessonItem()
//            taskContentItemIndex.value = 0
//        }
//        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
//        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setLessonIndex(newIndex: Int) {
//        if (0 <= newIndex &&
//            newIndex < courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.size ?: 0 &&
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                newIndex
//            ) != null
//        ) {
//            lessonContentItemIndex.value = newIndex
//            changeLessonItem()
//            taskContentItemIndex.value = 0
//        }
//        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
//        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun moveCourseIndex(delta: Int = 1) {
        if (0 <= courseContentItemIndex.value + delta && courseContentItemIndex.value + delta < courseContentData?.size ?: 0) {
            courseContentItemIndex.value += delta
            changeLessonItem()
            lessonContentItemIndex.value = 0
            taskContentItemIndex.value = 0
        }
    }

    fun setCourseId(newIndex: Int) {
        courseId.value = newIndex
        receiveCourseContentData(newIndex.toString())
        // changeLessonItem() TODO: использовать, когда changeLessonItem адаптируется под новую архитектуру
        Timber.i("courseIndex is $newIndex")

    }

    fun setCourseContentIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseContentData?.size ?: 0) {
            courseContentItemIndex.value = newIndex
            changeLessonItem()
            lessonContentItemIndex.value = 0
            taskContentItemIndex.value = 0
        }
        Timber.i("courseContentItemIndex: ${courseContentItemIndex.value}")
        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setIndexes(
        courseContentItemIndex: Int,
        lessonContentItemIndex: Int,
        taskContentItemIndex: Int
    ) {
        setCourseContentIndex(courseContentItemIndex)
        setLessonIndex(lessonContentItemIndex)
        setTaskIndex(taskContentItemIndex)
    }

    // Getters
//    fun getCourseTitle(): String {
//        return
//    }

    fun setToken(token: String) {
        this.token = token
    }

    fun setCourseName(courseName: String) {
        this.courseName = courseName
    }

    fun getCourseName(): String {
        return this.courseName
    }

    fun getLessonsTitles(): List<String>? {
        return courseContentData?.map { it.name ?: "" }
//        return listOf()
//        return courseContentData?.map { it.lessonTitle }.orEmpty()
    }

    fun getLessonsContents(index: Int? = null): List<CourseModule>? {
//        return listOf()
            return courseContentData?.get(index?: 0)?.modules?.toList()
//        return courseContentData?.get(
//            index ?: courseContentItemIndex.value
//        )?.lessonContent.orEmpty()
    }

    fun getTestLessonContent(): TestContentItems? {
        return testLessonContent
    }

    fun getArticleLessonContent(): ArticleContentItems? {
        return articleLessonContent
    }

    fun getCourseContentItemIndexState(): MutableState<Int> {
        return courseContentItemIndex
    }

    fun getLessonContentItemIndex(): MutableState<Int> {
        return lessonContentItemIndex
    }

    fun getTaskContentItemIndexState(): MutableState<Int> {
        return taskContentItemIndex
    }

    fun getAttemptKey(): MutableState<String> {
        return testAttemptKey
    }

    fun getCoursesContentSize(): Int {
        return courseContentData?.size ?: 0
    }

    fun getLessonContentSize(): Int {
        return 0
//        return courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.size
//            ?: 0
    }

    fun getTaskAttemptsCount(): Int {
        return 0

//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as TestContentItems?
//            )?.taskContent?.size ?: 0
    }

    fun getTaskTestsContentSize(): Int {
        return 0

//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as TestContentItems?
//            )?.taskContent?.get(testAttemptKey.value)?.second?.size ?: 0
    }

    fun getTaskArticlesContentSize(): Int {
        return 0

//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as ArticleContentItems?
//            )?.taskContent?.size ?: 0
    }

    fun getTaskType(): TaskType {
        return if (articleLessonContent != null)
            TaskType.LESSON
        else
            TaskType.QUIZ
    }

    fun getNextLessonType(): TaskType {
        return TaskType.NONE
//        return if (
//            lessonContentItemIndex.value + 1 < (courseContentData?.get(
//                courseContentItemIndex.value
//            )?.lessonContent?.size ?: 0)
//        )
//            (courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value + 1
//            )?.taskType ?: TaskType.NONE)
//        else
//            TaskType.NONE
    }

    fun getPrevLessonType(): TaskType {
        return TaskType.NONE
//        return if (0 <= lessonContentItemIndex.value - 1)
//            (courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value - 1
//            )?.taskType ?: TaskType.NONE)
//        else
//            TaskType.NONE
    }
}

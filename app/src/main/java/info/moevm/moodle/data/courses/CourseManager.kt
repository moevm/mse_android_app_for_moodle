package info.moevm.moodle.data.courses

import androidx.compose.runtime.MutableState
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.model.CurrentCourses
import info.moevm.moodle.ui.coursescreen.*
import kotlinx.coroutines.*
import timber.log.Timber



class CoursesManager(
    private var token: String,
    private val moodleApi: MoodleApi,
    private val courseIndex: MutableState<Int>,
    private val courseContentItemIndex: MutableState<Int>,
    private val lessonContentItemIndex: MutableState<Int>,
    private val taskContentItemIndex: MutableState<Int>,
    private val testAttemptKey: MutableState<String>,
) {
//    private var token: String // -- позже нужно добавить
    private val courseName: String = ""
    private var courseData: List<CourseContentItem>? = null
    private var coursesLoaded: Boolean = false
    private var articleLessonContent: ArticleContentItems? = null
    private var testLessonContent: TestContentItems? = null

    var requiredMoveLessonIndexForward: Boolean = false
    var requiredMoveLessonIndexBack: Boolean = false

    private fun changeLessonItem() {
        val lessonContent =
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value
            )
        when (lessonContent?.taskType) {
            TaskType.TOPIC -> {
                articleLessonContent = lessonContent as ArticleContentItems
                testLessonContent = null
            }
            TaskType.TEST -> {
                articleLessonContent = null
                testLessonContent = lessonContent as TestContentItems
            }
            else -> {
            }
        }
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

    fun receiveCourseContentData(/*token: String,*/ courseId: String) {
        var data: List<CourseContentData>? = null
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                data = moodleApi.getCourseContent(token, courseId)
            }
//            withContext(Dispatchers.IO) {
//                if (token != "") {
//                    val courseContentData = moodleApi.getCourseContent(token, courseId)
//                }
//                courseData = moodleApi.getFakeCourses(courseName)
//                coursesLoaded = true
//            }
            withContext(Dispatchers.Main) {
                changeLessonItem()
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
        if (0 <= lessonContentItemIndex.value + delta &&
            lessonContentItemIndex.value + delta < courseData?.get(
                courseContentItemIndex.value
            )?.lessonContent?.size ?: 0 &&
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value + delta
            ) != null
        ) {
            lessonContentItemIndex.value += delta
            changeLessonItem()
            taskContentItemIndex.value = 0
        }
        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setLessonIndex(newIndex: Int) {
        if (0 <= newIndex &&
            newIndex < courseData?.get(courseContentItemIndex.value)?.lessonContent?.size ?: 0 &&
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                newIndex
            ) != null
        ) {
            lessonContentItemIndex.value = newIndex
            changeLessonItem()
            taskContentItemIndex.value = 0
        }
        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun moveCourseIndex(delta: Int = 1) {
        if (0 <= courseContentItemIndex.value + delta && courseContentItemIndex.value + delta < courseData?.size ?: 0) {
            courseContentItemIndex.value += delta
            changeLessonItem()
            lessonContentItemIndex.value = 0
            taskContentItemIndex.value = 0
        }
    }

    fun setCourseIndex(newIndex: Int) {
        courseIndex.value = newIndex
        Timber.i("courseIndex is $newIndex")
    }

    fun setCourseContentIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseData?.size ?: 0) {
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

    fun getLessonsTitles(): List<String> {
        return courseData?.map { it.lessonTitle }.orEmpty()
    }

    fun getLessonsContents(index: Int? = null): List<LessonContentItem?> {
        return courseData?.get(
            index ?: courseContentItemIndex.value
        )?.lessonContent.orEmpty()
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
        return courseData?.size ?: 0
    }

    fun getLessonContentSize(): Int {
        return courseData?.get(courseContentItemIndex.value)?.lessonContent?.size
            ?: 0
    }

    fun getTaskAttemptsCount(): Int {
        return (
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value
            ) as TestContentItems?
            )?.taskContent?.size ?: 0
    }

    fun getTaskTestsContentSize(): Int {
        return (
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value
            ) as TestContentItems?
            )?.taskContent?.get(testAttemptKey.value)?.second?.size ?: 0
    }

    fun getTaskArticlesContentSize(): Int {
        return (
            courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value
            ) as ArticleContentItems?
            )?.taskContent?.size ?: 0
    }

    fun getTaskType(): TaskType {
        return if (articleLessonContent != null)
            TaskType.TOPIC
        else
            TaskType.TEST
    }

    fun getNextLessonType(): TaskType {
        return if (
            lessonContentItemIndex.value + 1 < (courseData?.get(
                courseContentItemIndex.value
            )?.lessonContent?.size ?: 0)
        )
            (courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value + 1
            )?.taskType ?: TaskType.NONE)
        else
            TaskType.NONE
    }

    fun getPrevLessonType(): TaskType {
        return if (0 <= lessonContentItemIndex.value - 1)
            (courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(
                lessonContentItemIndex.value - 1
            )?.taskType ?: TaskType.NONE)
        else
            TaskType.NONE
    }
}

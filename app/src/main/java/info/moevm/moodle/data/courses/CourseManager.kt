package info.moevm.moodle.data.courses

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.model.CurrentCourses
import info.moevm.moodle.ui.coursescontent.*
import kotlinx.coroutines.*
import timber.log.Timber

class CourseManager(
    private var token: String,
    private val moodleApi: MoodleApi,
    private val courseId: MutableState<Int>, // id курса
    private val categoryLessonItemIndex: MutableState<Int>, // index категории в курсе
    private val lessonItemIndex: MutableState<Int>, // index урока в категории
    private val taskItemIndex: MutableState<Int>, // index занятия в уроке
    private val testAttemptKey: MutableState<String> // index попытки
) {
    private var courseName: String = ""
    private var courseContentData: List<CourseMoodleContentData>? = null

    private var articleLesson: ArticleTaskContentItem? = null
    private var lessonPages: LessonPages? = null
    private var testLessonContent: TestContentItems? = null // TODO: убрать

    var requiredMoveLessonIndexForward: Boolean = false
    var requiredMoveLessonIndexBack: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    private fun BuildLessonContent(content: String?) { // TODO добавить обработку строки для удаления лишних '/'
        Box( // TODO добавить поддержку полноэкранного режима для видео
            modifier = Modifier.padding(10.dp)
        ) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    this.settings.javaScriptEnabled = true
                    this.loadData(content ?: "<p>Ошибка загрузки</p>", "text/html", "utf-8")
                }
            })
        }
    }

    fun changeLessonItem() {
        when (courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(lessonItemIndex.value)?.modname ?: "") {
            TaskType.LESSON.value -> { // TODO: починить
                articleLesson = ArticleTaskContentItem(
                    lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.title ?: "Ошибка загрузки",
                    TaskContentType.ARTICLE,
                    "",
                    TaskStatus.WORKING,
                    { BuildLessonContent(lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.contents) }
                )
                testLessonContent = null
            }
            TaskType.QUIZ.value -> {
//                articleLessonContent = null
//                testLessonContent = lessonContent as TestContentItems
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

    fun receiveCourseContentData(courseId: String) { // считывает содержимое одного курса
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                courseContentData = moodleApi.getCourseContent(token, courseId)
            }
        }
    }

    fun loadLessonPages(lessonId: Int) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                lessonPages = moodleApi.getLessonPages(token, lessonId.toString())
            }
        }
        val time = System.currentTimeMillis() // TODO заменить
        while (true) {
            if (System.currentTimeMillis() - time > 300)
                break
        }
    }

    fun getLessonPage(pageIndex: Int) : LessonPage? {
        return lessonPages?.pages?.getOrNull(pageIndex)?.page
    }

//     Setters
    fun moveTaskIndex(delta: Int = 1) {
        setTaskIndex(taskItemIndex.value + delta)
    }

    fun setTaskIndex(newIndex: Int) {
        if (articleLesson != null) {
            if (0 <= newIndex && newIndex < lessonPages?.pages?.size ?: 0)
                taskItemIndex.value = newIndex
        } else if (testLessonContent != null) {
//            if (0 <= newIndex && newIndex < testLessonContent?.taskContent?.get(
//                    testAttemptKey.value
//                )?.second?.size ?: 0
//            )
//                taskContentItemIndex.value = newIndex
        }
        Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
    }

    fun setAttemptKey(newTestAttemptKey: String) {
//        if (testLessonContent != null && testLessonContent?.taskContent.orEmpty()
//                .contains(newTestAttemptKey)
//        ) {
//            testAttemptKey.value = newTestAttemptKey
//            taskContentItemIndex.value = 0
//        }
//        Timber.i("testAttemptKey: ${testAttemptKey.value}")
    }

    fun moveLessonIndex(delta: Int = 1) {
        setLessonIndex(lessonItemIndex.value + delta)
    }

    fun setLessonIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.size ?: 0 &&
            courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(newIndex) != null
        ) {
            lessonItemIndex.value = newIndex
        }
        Timber.i("lessonContentItemIndex: ${lessonItemIndex.value}")
    }

    fun setCategoryLessonIndex(newIndex: Int) {
        if (0 <= categoryLessonItemIndex.value && categoryLessonItemIndex.value < courseContentData?.size ?: 0) {
            categoryLessonItemIndex.value = newIndex
            lessonItemIndex.value = 0
            taskItemIndex.value = 0
        }
    }

    fun setCourseId(newIndex: Int) {
        courseId.value = newIndex
        receiveCourseContentData(newIndex.toString())
        // changeLessonItem() TODO: использовать, когда changeLessonItem адаптируется под новую архитектуру
        Timber.i("courseIndex is $newIndex")

    }

    fun setCourseContentIndex(newIndex: Int) {
//        if (0 <= newIndex && newIndex < courseContentData?.size ?: 0) {
//            courseContentItemIndex.value = newIndex
//            changeLessonItem()
//            lessonContentItemIndex.value = 0
//            taskContentItemIndex.value = 0
//        }
//        Timber.i("courseContentItemIndex: ${courseContentItemIndex.value}")
//        Timber.i("lessonContentItemIndex: ${lessonContentItemIndex.value}")
//        Timber.i("taskContentItemIndex: ${taskContentItemIndex.value}")
    }

    fun setIndexes(
        courseContentItemIndex: Int,
        lessonContentItemIndex: Int,
        taskContentItemIndex: Int
    ) {
//        setCourseContentIndex(courseContentItemIndex)
//        setLessonIndex(lessonContentItemIndex)
//        setTaskIndex(taskContentItemIndex)
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
            return courseContentData?.getOrNull(index?: -1)?.modules?.toList()
//        return courseContentData?.get(
//            index ?: courseContentItemIndex.value
//        )?.lessonContent.orEmpty()
    }
    fun getLessonsItemInstanceId(indexCategory: Int?, indexLesson: Int?) : Int? {
        return courseContentData?.getOrNull(indexCategory?: -1)?.modules?.getOrNull(indexLesson?: -1)?.instance
    }






    fun getTestLessonContent(): TestContentItems? {
        return testLessonContent
    }

    fun getArticleLessonContentItem(): ArticleTaskContentItem? {
        return articleLesson
    }

//    fun getCourseContentItemIndexState(): MutableState<Int> {
//        return courseContentItemIndex
//    }

    fun getLessonContentItemIndex(): MutableState<Int> {
        return /*lessonContentItemIndex*/ mutableStateOf(0)
    }

    fun getTaskContentItemIndexState(): MutableState<Int> {
        return /* taskContentItemIndex */ mutableStateOf(0)
    }

    fun getAttemptKey(): MutableState<String> {
        return /* testAttemptKey */ mutableStateOf("0")
    }

    fun getCategoryLessonSize(): Int {
        return courseContentData?.size ?: 0
    }

    fun getLessonContentSize(): Int {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.size ?: 0
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
        return lessonPages?.pages?.size ?: 0


//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as ArticleContentItems?
//            )?.taskContent?.size ?: 0
    }

    fun getTaskType(indexCategory: Int?, indexLesson: Int?): TaskType {
        return when (courseContentData?.getOrNull(indexCategory ?: -1)?.modules?.getOrNull(indexLesson ?: -1)?.modname ?: "") {
            TaskType.LESSON.value -> TaskType.LESSON
            TaskType.QUIZ.value -> TaskType.QUIZ
            else -> TaskType.NONE
        }
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

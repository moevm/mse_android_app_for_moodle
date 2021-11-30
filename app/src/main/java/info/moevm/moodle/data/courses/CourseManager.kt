package info.moevm.moodle.data.courses

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
    private val testAttemptKey: MutableState<Int> // ключ попытки
) {
    private var courseName: String = ""
    private var courseContentData: List<CourseMoodleContentData>? = null

    private var articleLesson: ArticleTaskContentItem? = null
    private var lessonPages: LessonPages? = null

    private var quizAttemptContent: QuizAttempts? = null
    private var testTaskContentItem: TestTaskContentItem? = null
    private var quizInProgressContent: QuizInProgress? = null
    private var quizFinishedContent: QuizFinished? = null

    var requiredMoveLessonIndexForward: Boolean = false
    var requiredMoveLessonIndexBack: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    private fun BuildLessonContent(content: String?) { // TODO добавить обработку мат формул и подобного
        Box( // TODO добавить поддержку полноэкранного режима для видео
            modifier = Modifier.padding(10.dp)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        this.settings.javaScriptEnabled = true
                        this.loadData(content ?: "<p>Ошибка загрузки</p>", "text/html", "utf-8")
                    }
                },
                update = {
                    it.loadData(content ?: "<p>Ошибка загрузки</p>", "text/html", "utf-8")
                }
            )
        }
    }

    fun changeLessonItem(isFinished: Boolean = false) {
        when (courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(lessonItemIndex.value)?.modname ?: "") {
            TaskType.LESSON.value -> { // TODO: починить
                articleLesson = ArticleTaskContentItem(
                    lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.title ?: "Ошибка загрузки",
                    TaskContentType.ARTICLE,
                    "",
                    TaskStatus.WORKING,
                    { BuildLessonContent(lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.contents) }
                )
                quizFinishedContent = null
                quizInProgressContent = null
                quizAttemptContent = null
            }
            TaskType.QUIZ.value -> {
                if (isFinished)
                    testTaskContentItem = TestTaskContentItem(
                        "Элемент теста",
                        TaskContentType.UNSUPPORTED,
                        quizFinishedContent?.questions?.getOrNull(0)?.mark ?: "Ошибка загрузки",
                        TaskStatus.DONE,
                        { BuildLessonContent(content = quizFinishedContent?.questions?.getOrNull(0)?.html) } // FIXME доработать
                    )
                else
                    testTaskContentItem = TestTaskContentItem(
                        "Элемент теста",
                        TaskContentType.UNSUPPORTED,
                        quizInProgressContent?.questions?.getOrNull(0)?.mark ?: "Ошибка загрузки",
                        TaskStatus.DONE,
                        { BuildLessonContent(content = quizInProgressContent?.questions?.getOrNull(0)?.html) } // FIXME доработать
                    )
                articleLesson = null
            }
            else -> { }
        }
    }

    fun receiveCurrentCourseTitles(): CurrentCourses? {
        var data: CurrentCourses? = null
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (token != "") {
                    data = moodleApi.getCurrentCourses(token)
                }
                loaded = true
            }
            val time = System.currentTimeMillis()
            while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
        }
        return data
    }

    fun receiveCourseContentData(courseId: String) { // считывает содержимое одного курса
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                courseContentData = moodleApi.getCourseContent(token, courseId)
                loaded = true
            }
            val time = System.currentTimeMillis()
            while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
        }
    }

    fun receiveLessonPages(lessonId: Int) {
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                lessonPages = moodleApi.getLessonPages(token, lessonId.toString())
                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
    }

    fun receiveQuizAttempts(quizid: String, status: String = "all") {
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizAttemptContent = moodleApi.getQuizAttempts(token, quizid, status)
                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
    }

    fun receiveQuizInProgress(attemptid: String, page: String) { //
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizInProgressContent = moodleApi.getQuizInProgress(token, attemptid, page)
                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
    }

    fun receiveQuizFinished(attemptid: String, page: String) {
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizFinishedContent = moodleApi.getQuizFinished(token, attemptid, page)
                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
    }

    fun startNewAttempt(quizid: String): Boolean { // FIXME не работает, возвращает null вместо попытки
        var loaded = false
        var newAttempt = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val response = moodleApi.startNewAttempt(token, quizid)
                if (response?.attempts != null) {
                    newAttempt = true
//                    quizAttemptContent?.attempts?.add(response.attempts[0])
                } else
                    newAttempt = false

                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
        return newAttempt
    }

    fun moveTaskIndex(delta: Int = 1) {
        setTaskIndex(taskItemIndex.value + delta)
    }

    fun setTaskIndex(newIndex: Int) {
        if (articleLesson != null) {
            if (0 <= newIndex && newIndex < lessonPages?.pages?.size ?: 0)
                taskItemIndex.value = newIndex
        } /* else if (quizContent != null) {
//            if (0 <= newIndex && newIndex < testLessonContent?.taskContent?.get(
//                    testAttemptKey.value
//                )?.second?.size ?: 0
//            )
//                taskContentItemIndex.value = newIndex
        }*/
        Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
    }

    fun setAttemptKey(newTestAttemptKey: Int) {
        testAttemptKey.value = newTestAttemptKey
        taskItemIndex.value = 0
//        if (testLessonContent != null && testLessonContent?.taskContent.orEmpty()
//                .contains(newTestAttemptKey)
//        ) {
//            testAttemptKey.value = newTestAttemptKey
//            taskContentItemIndex.value = 0
//        }
        Timber.i("testAttemptKey: ${testAttemptKey.value}")
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
        lessonItemIndex.value = 0
        taskItemIndex.value = 0
        receiveCourseContentData(newIndex.toString())
        changeLessonItem()
        Timber.i("courseIndex is $newIndex")
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun setCourseName(courseName: String) {
        this.courseName = courseName
    }

    fun getLessonPage(pageIndex: Int): LessonPage? {
        return lessonPages?.pages?.getOrNull(pageIndex)?.page
    }

    fun getCurrentLessonURL(): String? {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(lessonItemIndex.value)?.url
    }

    fun getCourseName(): String {
        return this.courseName
    }

    fun getLessonsTitles(): List<String>? {
        return courseContentData?.map { it.name ?: "" }
    }

    fun getLessonsContents(index: Int? = null): List<CourseModule>? {
        return courseContentData?.getOrNull(index ?: -1)?.modules?.toList()
    }

    fun getLessonsItemInstanceId(indexCategory: Int?, indexLesson: Int?): Int? {
        return courseContentData?.getOrNull(indexCategory ?: -1)?.modules?.getOrNull(indexLesson ?: -1)?.instance
    }

    fun getQuizAttemptContent(): QuizAttempts? {
        return quizAttemptContent
    }

    fun getQuizInProgressContent(): QuizInProgress? {
        return quizInProgressContent
    }

    fun getQuizFinishedContent(): QuizFinished? {
        return quizFinishedContent
    }

    fun getArticleLessonContentItem(): ArticleTaskContentItem? {
        return articleLesson
    }

    fun getLessonContentItemIndex(): MutableState<Int> {
        return lessonItemIndex
    }

    fun getTaskContentItemIndexState(): MutableState<Int> {
        return taskItemIndex
    }

    fun getAttemptKey(): MutableState<Int> {
        return testAttemptKey
    }

    fun getCategoryLessonSize(): Int {
        return courseContentData?.size ?: 0
    }

    fun getLessonContentSize(): Int {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.size ?: 0
    }

    fun getTaskAttemptsCount(): Int {
        return TODO()

//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as TestContentItems?
//            )?.taskContent?.size ?: 0
    }

    fun getTaskTestsContentSize(): Int {
        return TODO()
//        return 0
//        return (
//            courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value
//            ) as TestContentItems?
//            )?.taskContent?.get(testAttemptKey.value)?.second?.size ?: 0
    }

    fun getTaskArticlesContentSize(): Int {
        return lessonPages?.pages?.size ?: 0
    }

    fun getTaskType(indexCategory: Int?, indexLesson: Int?): TaskType {
        return when (courseContentData?.getOrNull(indexCategory ?: -1)?.modules?.getOrNull(indexLesson ?: -1)?.modname ?: "") {
            TaskType.LESSON.value -> TaskType.LESSON
            TaskType.QUIZ.value -> TaskType.QUIZ
            else -> TaskType.NONE
        }
    }

    fun getNextLessonType(): TaskType {
        return TODO()
//        return TaskType.NONE
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
        return TODO()
//        return if (0 <= lessonContentItemIndex.value - 1)
//            (courseContentData?.get(courseContentItemIndex.value)?.lessonContent?.get(
//                lessonContentItemIndex.value - 1
//            )?.taskType ?: TaskType.NONE)
//        else
//            TaskType.NONE
    }
}

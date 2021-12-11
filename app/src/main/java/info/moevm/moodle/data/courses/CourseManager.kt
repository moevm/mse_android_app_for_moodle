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
import java.lang.Math.max

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

    fun changeGlobalLessonItem(isFinished: Boolean = false) {
        when (courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(lessonItemIndex.value)?.modname ?: "") {
            TaskType.LESSON.value -> {
                articleLesson = ArticleTaskContentItem(
                    lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.title ?: "Данный элемент не поддерживается",
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
                        TaskContentType.TEST_FINISHED,
                        quizFinishedContent?.questions?.getOrNull(0)?.mark ?: "Данный элемент не поддерживается",
                        TaskStatus.DONE,
                        { BuildLessonContent(content = quizFinishedContent?.questions?.getOrNull(0)?.html) } // FIXME доработать
                    )
                else
                    testTaskContentItem = TestTaskContentItem(
                        "Элемент теста",
                        TaskContentType.TEST_IN_PROGRESS,
                        quizInProgressContent?.questions?.getOrNull(0)?.mark ?: "Данный элемент не поддерживается",
                        TaskStatus.DONE,
                        { BuildLessonContent(content = quizInProgressContent?.questions?.getOrNull(0)?.html) } // FIXME доработать
                    )
                articleLesson = null
            }
            else -> { }
        }
    }

    fun changeLocalTestItem() {
        if (quizFinishedContent != null) {
            testTaskContentItem = TestTaskContentItem(
                "Элемент теста",
                TaskContentType.TEST_FINISHED,
                quizFinishedContent?.questions?.getOrNull(taskItemIndex.value)?.mark ?: "Данный элемент не поддерживается",
                TaskStatus.DONE,
                { BuildLessonContent(content = quizFinishedContent?.questions?.getOrNull(taskItemIndex.value)?.html) } // FIXME доработать
            )
        } else if (quizInProgressContent != null) {
            testTaskContentItem = TestTaskContentItem(
                "Элемент теста",
                TaskContentType.TEST_IN_PROGRESS,
                quizInProgressContent?.questions?.getOrNull(0)?.mark ?: "Данный элемент не поддерживается", // 0, т.к всегда загружается 1 вопрос
                TaskStatus.DONE,
                { BuildLessonContent(content = quizInProgressContent?.questions?.getOrNull(0)?.html) } // FIXME доработать
            )
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

    fun receiveQuizInProgress(attemptid: String, page: String) {
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizInProgressContent = moodleApi.getQuizInProgress(token, attemptid, page)
                quizFinishedContent = null
                loaded = true
            }
        }
        val time = System.currentTimeMillis()
        while (!loaded && System.currentTimeMillis() - time < 2000) {} // FIXME заменить 2с на другое или константу
    }

    fun receiveQuizFinished(attemptid: String) {
        var loaded = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizFinishedContent = moodleApi.getQuizFinished(token, attemptid)
                quizInProgressContent = null
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
                if (response != null) {
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

    fun moveTaskIndex(delta: Int = 1): Boolean {
        return setTaskIndex(taskItemIndex.value + delta)
    }

    fun setTaskIndex(newIndex: Int): Boolean {
        if (isRealPage(newIndex)) {
            taskItemIndex.value = newIndex
            Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
            return true
        }
        Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
        return false
    }

    fun isRealPage(newIndex: Int): Boolean {
        if (articleLesson != null) {
            if (0 <= newIndex && newIndex < lessonPages?.pages?.size ?: 0)
                return true
        } else if (quizFinishedContent != null) {
            if (0 <= newIndex && newIndex < quizFinishedContent?.questions?.size ?: 0)
                return true
        } else if (quizInProgressContent != null) {
            if (0 <= newIndex && newIndex <= max(quizInProgressContent?.nextpage ?: 0, taskItemIndex.value)) // нужна верхняя граница max, т.к у moodle конец страниц равен -1
                return true
        }
        return false
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
        changeGlobalLessonItem()
        Timber.i("courseIndex is $newIndex")
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun setCourseName(courseName: String) {
        this.courseName = courseName
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

    fun getArticleLessonContentItem(): ArticleTaskContentItem? {
        return articleLesson
    }

    fun getTestTaskContentItem(): TestTaskContentItem? {
        return testTaskContentItem
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

    fun getLessonContentSize(): Int {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.size ?: 0
    }

    fun getTaskArticlesContentSize(): Int {
        return lessonPages?.pages?.size ?: 0
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
}

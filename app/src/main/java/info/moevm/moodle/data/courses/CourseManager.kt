package info.moevm.moodle.data.courses

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.lang.Math.max
import java.util.concurrent.TimeUnit

class CourseManager(
    private var token: String,
    private val moodleApi: MoodleApi,
    private val courseId: MutableState<Int>, // id курса
    private val categoryLessonItemIndex: MutableState<Int>, // index категории в курсе
    private val lessonItemIndex: MutableState<Int>, // index урока в категории
    private val taskItemIndex: MutableState<Int>, // index занятия в уроке
    private val testAttemptId: MutableState<Int> // ключ попытки
) {
    private var courseName: String = ""
    private var courseContentData: List<CourseMoodleContentData>? = null

    private var articleLesson: ArticleTaskContentItem? = null
    private var lessonPages: LessonPages? = null

    private var quizAttemptContent: QuizAttempts? = null
    private var quizTaskContentItem: QuizTaskContentItem? = null
    private var quizInProgressContent: QuizInProgress? = null
    private var quizFinishedContent: QuizFinished? = null

    var webView: WebView? = null

    /**
     * Удаляем кнопку "проверить", которая есть в html (её добавляет Moodle)
     */
    private fun removeButtonFromHTML(str: String?): String? {
        val foundStartPosition: Pair<Int, String> =
            str?.findLastAnyOf(List(1) { "<input type=\"submit\"" })
                ?: return str
        val foundLastPosition: Pair<Int, String> =
            str.findAnyOf(List(1) { "/>" }, foundStartPosition.first)
                ?: return str
        return str.removeRange(
            foundStartPosition.first,
            foundLastPosition.first + 2
        )
    }

    /**
     * Интерфейс для взаимодействия с элементами html
     */
    private inner class MoodleJavaScriptInterface {
        private var attemptid = ""
        private var slot = ""
        private var sequncecheck = ""
        fun reloadParams() {
            attemptid = testAttemptId.value.toString()
            slot = (quizInProgressContent?.questions?.get(0)?.slot ?: -1).toString()
            sequncecheck =
                (quizInProgressContent?.questions?.get(0)?.sequencecheck ?: -1).toString()
        }

        // один ответ
        @android.webkit.JavascriptInterface
        fun postRadioButtonResult(idButton: String) {
            if (idButton.isEmpty())
                return
            reloadParams()
            val answer: MutableMap<String, String> = mutableMapOf()
            answer["data[0][name]"] = "slots"
            answer["data[0][value]"] = slot
            answer["data[1][name]"] = idButton.split("_")[0] + "_:sequencecheck"
            answer["data[1][value]"] = sequncecheck
            val indexFirstR = idButton.indexOfFirst { it == 'r' }
            answer["data[2][name]"] = idButton.take(indexFirstR + 1)
            answer["data[2][value]"] = idButton.drop(indexFirstR + 1)
            requireQuizSaveStep(attemptid, answer)
            receiveQuizInProgress(attemptid, getTaskContentItemIndexState().value.toString())
        }

        // несколько ответов
        @android.webkit.JavascriptInterface
        fun postCheckBoxResult(resString: String) {
            if (resString.isEmpty())
                return
            reloadParams()
            val arrayTokensResult = resString.split(';').dropLast(1) // удаляем лишнюю строку "", которая возникает из-за ';'
            val answer: MutableMap<String, String> = mutableMapOf()
            answer["data[0][name]"] = "slots"
            answer["data[0][value]"] = slot
            answer["data[1][name]"] = arrayTokensResult[0].split("_")[0] + "_:sequencecheck"
            answer["data[1][value]"] = sequncecheck
            var counter = 2
            for (answerToken in arrayTokensResult) {
                answer["data[$counter][name]"] = answerToken
                answer["data[$counter][value]"] = "1"
                counter++
            }
            requireQuizSaveStep(attemptid, answer)
            receiveQuizInProgress(attemptid, getTaskContentItemIndexState().value.toString())
        }

        // текстовый ответ
        @android.webkit.JavascriptInterface
        fun postTextResult(resText: String) {
            if (resText.isEmpty())
                return
            reloadParams()
            val arrayTokensResult = resText.split(';')
            val answer: MutableMap<String, String> = mutableMapOf()
            answer["data[0][name]"] = "slots"
            answer["data[0][value]"] = slot
            answer["data[1][name]"] = arrayTokensResult[0].split("_")[0] + "_:sequencecheck"
            answer["data[1][value]"] = sequncecheck
            answer["data[2][name]"] = arrayTokensResult[0]
            answer["data[2][value]"] = arrayTokensResult[1]
            requireQuizSaveStep(attemptid, answer)
            receiveQuizInProgress(attemptid, getTaskContentItemIndexState().value.toString())
        }

        // численный ответ
        @android.webkit.JavascriptInterface
        fun postNumericalResult(resText: String) {
            if (resText.isEmpty())
                return
            val arrayTokensResult = resText.split(';')
            if (arrayTokensResult[1].matches("^-?\\d+(\\.\\d+)?$".toRegex())) {
                postTextResult(resText) // если число, то можно сохранить как "строку", иначе неверный ответ
            }
            // TODO хорошо бы иначе выводить сообщение, что нужно число
        }

        // выбор соответствий
        @android.webkit.JavascriptInterface
        fun postMatchesResult(resString: String) {
            if (resString.isEmpty())
                return
            reloadParams()
            val arrayTokensResult = resString.split(';').dropLast(1) // удаляем лишнюю строку "", которая возникает из-за ';'
            val answer: MutableMap<String, String> = mutableMapOf()
            answer["data[0][name]"] = "slots"
            answer["data[0][value]"] = slot
            answer["data[1][name]"] = arrayTokensResult[0].split("_")[0] + "_:sequencecheck"
            answer["data[1][value]"] = sequncecheck
            var counter = 2 // индексирования для запроса
            for (i in arrayTokensResult.indices step 2) {
                answer["data[$counter][name]"] = arrayTokensResult[i]
                answer["data[$counter][value]"] = arrayTokensResult[i + 1]
                counter++
            }
            requireQuizSaveStep(attemptid, answer)
            receiveQuizInProgress(attemptid, getTaskContentItemIndexState().value.toString())
        }
    }

    /**
     *  Построение содержимого экрана теста/лекции
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    @Composable
    private fun BuildLessonContent(
        content: String?,
        addOwnChecker: Boolean
    ) { // TODO добавить обработку мат формул и подобного
        Column {
            Box( // TODO добавить поддержку полноэкранного режима для видео
                modifier = Modifier.padding(5.dp, top = 0.dp, bottom = 0.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        webView = WebView(context)
                        webView!!.apply {
                            this.settings.javaScriptEnabled = true
                            this.addJavascriptInterface(MoodleJavaScriptInterface(), "android")
                            if (addOwnChecker)
                                this.loadData(
                                    removeButtonFromHTML(content) + addCheckers(),
                                    "text/html",
                                    "utf-8"
                                )
                            else
                                this.loadData(
                                    content
                                        ?: "<p>Ошибка загрузки</p>",
                                    "text/html",
                                    "utf-8"
                                )
                        }
                    },
                    update = {
                        if (addOwnChecker)
                            it.loadData(
                                removeButtonFromHTML(content) + addCheckers(),
                                "text/html",
                                "utf-8"
                            )
                        else
                            it.loadData(
                                content
                                    ?: "<p>Ошибка загрузки</p>",
                                "text/html",
                                "utf-8"
                            )
                    }
                )
            }
        }
    }

    /**
     * Режим ожидания на заданное время или пока флаг не станет true (нужно для получения данных от Moodle)
     */
    private fun waitSomeSecondUntilFalse(flag: MutableStateFlow<Boolean>, seconds: Long) {
        val time = System.currentTimeMillis()
        val waitingTime = TimeUnit.SECONDS.toMillis(seconds)
        while (!flag.value && System.currentTimeMillis() - time < waitingTime) {}
    }

    /**
     * Глобальная установка элемента (устанавливает типаж теста или лекцию)
     */
    fun setGlobalItem(isFinished: Boolean = false) {
        when (
            courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(lessonItemIndex.value)?.modname ?: ""
        ) {
            TaskType.LESSON.value -> {
                articleLesson = ArticleTaskContentItem(
                    lessonPages?.pages?.getOrNull(taskItemIndex.value)?.page?.title ?: "Данный элемент не поддерживается",
                    TaskContentType.ARTICLE,
                    "" // Оценки нет, так как лекция
                ) {
                    BuildLessonContent(
                        content = lessonPages?.pages?.getOrNull(
                            taskItemIndex.value
                        )?.page?.contents,
                        addOwnChecker = false
                    )
                }
                quizFinishedContent = null
                quizInProgressContent = null
                quizAttemptContent = null
            }
            TaskType.QUIZ.value -> {
                if (isFinished)
                    quizTaskContentItem = QuizTaskContentItem(
                        "Элемент теста",
                        TaskContentType.TEST_FINISHED,
                        quizFinishedContent?.questions?.getOrNull(0)?.mark
                            ?: "Данный элемент не поддерживается"
                    ) {
                        BuildLessonContent(
                            content = quizFinishedContent?.questions?.getOrNull(
                                0
                            )?.html,
                            addOwnChecker = false
                        )
                    }
                else
                    quizTaskContentItem = QuizTaskContentItem(
                        "Элемент теста",
                        TaskContentType.TEST_IN_PROGRESS,
                        quizInProgressContent?.questions?.getOrNull(0)?.mark
                            ?: "Данный элемент не поддерживается"
                    ) {
                        BuildLessonContent(
                            content = quizInProgressContent?.questions?.getOrNull(
                                0
                            )?.html,
                            addOwnChecker = true
                        )
                    }
                articleLesson = null
            }
            else -> { /* TODO добавить обработку */ }
        }
    }

    /**
     * Смена элемента теста (в пределах одной группы: завершённые/в процессе)
     */
    fun changeLocalTestItem() {
        if (quizFinishedContent != null) {
            quizTaskContentItem = QuizTaskContentItem(
                "Элемент теста",
                TaskContentType.TEST_FINISHED,
                quizFinishedContent?.questions?.getOrNull(taskItemIndex.value)?.mark
                    ?: "Данный элемент не поддерживается"
            ) {
                BuildLessonContent(
                    content = quizFinishedContent?.questions?.getOrNull(
                        taskItemIndex.value
                    )?.html,
                    addOwnChecker = false
                )
            }
        } else if (quizInProgressContent != null) {
            quizTaskContentItem = QuizTaskContentItem(
                "Элемент теста",
                TaskContentType.TEST_IN_PROGRESS,
                quizInProgressContent?.questions?.getOrNull(0)?.mark
                    ?: "Данный элемент не поддерживается" // 0, т.к всегда загружается 1 вопрос
            ) {
                BuildLessonContent(
                    content = quizInProgressContent?.questions?.getOrNull(
                        0
                    )?.html,
                    addOwnChecker = true
                )
            }
        }
    }

    /**
     * Запрос на сохранение данных элемента теста (не даёт запрос Moodle на проверку ответа)
     */
    private fun requireQuizSaveStep(
        attemptId: String,
        answers: Map<String, String>
    ): AnswerSendResult? {
        var data: AnswerSendResult? = null
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (token != "") {
                    data = moodleApi.requireQuizSaveStep(
                        token,
                        attemptId,
                        answers
                    )
                }
                loaded.value = true
            }
            waitSomeSecondUntilFalse(loaded, 2)
        }
        return data
    }

    /**
     * Запрос на сохранение элемента теста
     */
    fun requireSaveCurrentTestStep() {
        when (quizInProgressContent?.questions?.get(0)?.type) {
            TestStepType.MULTICHOICE.value -> {
                when {
                    quizInProgressContent?.questions?.get(0)?.html?.findLastAnyOf(
                        List(1) { "type=\"radio\"" }
                    )?.first ?: 0 != 0 -> { // выбор одного ответа
                        webView?.evaluateJavascript("javascript:findCheckedRadio()") {}
                    }
                    quizInProgressContent?.questions?.get(0)?.html?.findLastAnyOf(
                        List(1) { "type=\"checkbox\"" }
                    )?.first ?: 0 != 0 -> { // выбор нескольких ответов
                        webView?.evaluateJavascript("javascript:findCheckBox()") {}
                    }
                    else -> {
                        Timber.w("Не распознан тип multichoice тестового задания")
                    }
                }
            }
            TestStepType.SHORTANSWER.value -> {
                webView?.evaluateJavascript("javascript:findTextAnswer()") {}
            }
            TestStepType.NUMERICAL.value -> { // дополнительно проверяет текст, является ли он числом
                webView?.evaluateJavascript("javascript:findNumericalAnswer()") {}
            }
            TestStepType.MATCH.value -> {
                webView?.evaluateJavascript("javascript:findMatches()") {}
            }
            else -> { // такой тип не поддерживается
                Timber.w("Не найден типаж тестового задания")
                // TODO добавить вывод сообщения об ошибке
            }
        }
    }

    /**
     * Получить список заголовков курсов, на которые записан пользователь
     */
    fun receiveCurrentCourseTitles(): CurrentCourses? {
        var data: CurrentCourses? = null
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (token != "") {
                    data = moodleApi.getCurrentCourses(token)
                }
                loaded.value = true
            }
            waitSomeSecondUntilFalse(loaded, 2)
        }
        return data
    }

    /**
     * Получить содержание одного курса
     */
    fun receiveCourseContentData(courseId: String) {
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                courseContentData = moodleApi.getCourseContent(token, courseId)
                loaded.value = true
            }
            waitSomeSecondUntilFalse(loaded, 2)
        }
    }

    /**
     * Получить страницы данной лекции
     */
    fun receiveLessonPages(lessonId: Int) {
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                lessonPages =
                    moodleApi.getLessonPages(token, lessonId.toString())
                loaded.value = true
            }
        }
        waitSomeSecondUntilFalse(loaded, 2)
    }

    /**
     * Получить попытки пользователя для теста
     */
    fun receiveQuizAttempts(quizid: String, status: String = "all") {
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizAttemptContent =
                    moodleApi.getQuizAttempts(token, quizid, status)
                loaded.value = true
            }
        }
        waitSomeSecondUntilFalse(loaded, 2)
    }

    /**
     * Получить содержимое страницы теста, когда он в режиме "в процессе"
     */
    fun receiveQuizInProgress(attemptid: String, page: String) {
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizInProgressContent =
                    moodleApi.getQuizInProgress(token, attemptid, page)
                quizFinishedContent = null
                loaded.value = true
            }
        }
        waitSomeSecondUntilFalse(loaded, 2)
    }

    /**
     * Получить содержимое тесто (полного), когда он в режиме "завершён"
     */
    fun receiveQuizFinished(attemptid: String) {
        val loaded = MutableStateFlow(false)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                quizFinishedContent =
                    moodleApi.getQuizFinished(token, attemptid)
                quizInProgressContent = null
                loaded.value = true
            }
        }
        waitSomeSecondUntilFalse(loaded, 2)
    }

    /**
     * Создать новую попытку
     */
    fun startNewAttempt(quizid: String): Boolean { // FIXME не работает, возвращает null вместо попытки
        TODO("New Attempt")
        val loaded = MutableStateFlow(false)
        var newAttempt = false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
//                val response = moodleApi.startNewAttempt(token, quizid)
//                if (response != null) {
//                    newAttempt = true
//                    quizAttemptContent?.attempts?.add(response.attempts[0])
//                } else
//                    newAttempt = false

                loaded.value = true
            }
        }
//        waitSomeSecondUntilFalse(loaded, 2)
        return newAttempt
    }

    /**
     * Сдвинуть индекс страницы урока на delta (допускаются отрицательные числа)
     */
    fun moveTaskIndex(delta: Int = 1): Boolean {
        return setTaskIndex(taskItemIndex.value + delta)
    }

    /**
     * Установить значение индекса страницы урока
     */
    fun setTaskIndex(newIndex: Int): Boolean {
        if (isRealPage(newIndex)) {
            taskItemIndex.value = newIndex
            Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
            return true
        }
        Timber.i("taskContentItemIndex: ${taskItemIndex.value}")
        return false
    }

    /**
     * Удобная проверка на существование страницы в пределах одного урока (не подходит для проверки теста в режиме "в процессе", если разница индексов не 1
     */
    fun isRealPage(newIndex: Int): Boolean {
        if (articleLesson != null) {
            if (0 <= newIndex && newIndex < lessonPages?.pages?.size ?: 0)
                return true
        } else if (quizFinishedContent != null) {
            if (0 <= newIndex && newIndex < quizFinishedContent?.questions?.size ?: 0)
                return true
        } else if (quizInProgressContent != null) {
            if (0 <= newIndex && newIndex <= max(
                    quizInProgressContent?.nextpage ?: 0, taskItemIndex.value
                )
            ) // нужна верхняя граница max, т.к у moodle конец страниц равен -1
                return true
        }
        return false
    }

    /**
     * Установить индекс урока
     */
    fun setLessonIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseContentData?.getOrNull(
                categoryLessonItemIndex.value
            )?.modules?.size ?: 0 &&
            courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(newIndex) != null
        ) {
            lessonItemIndex.value = newIndex
        }
        Timber.i("lessonContentItemIndex: ${lessonItemIndex.value}")
    }

    /**
     * Установить индекс группы
     */
    fun setCategoryLessonIndex(newIndex: Int) {
        if (0 <= categoryLessonItemIndex.value && categoryLessonItemIndex.value < courseContentData?.size ?: 0) {
            categoryLessonItemIndex.value = newIndex
            lessonItemIndex.value = 0
            taskItemIndex.value = 0
        }
    }

    /**
     * Установить индекс курса
     */
    fun setCourseId(newIndex: Int) {
        courseId.value = newIndex
        lessonItemIndex.value = 0
        taskItemIndex.value = 0
        receiveCourseContentData(newIndex.toString())
        setGlobalItem()
        Timber.i("courseIndex is $newIndex")
    }

    /**
     * Установить значение токена пользователя
     */
    fun setToken(token: String) {
        this.token = token
    }

    /**
     * Установить название курса
     */
    fun setCourseName(courseName: String) {
        this.courseName = courseName
    }

    /**
     * Получить ссылку на элемент урока
     */
    fun getCurrentLessonURL(): String? {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.getOrNull(
            lessonItemIndex.value
        )?.url
    }

    /**
     * Получить название курса
     */
    fun getCourseName(): String {
        return this.courseName
    }

    /**
     * Получить название уроков
     */
    fun getLessonsTitles(): List<String>? {
        return courseContentData?.map { it.name ?: "" }
    }

    /**
     * Получить содержимое уроков
     */
    fun getLessonsContents(index: Int? = null): List<CourseModule>? {
        return courseContentData?.getOrNull(index ?: -1)?.modules?.toList()
    }

    /**
     * Получить id урока
     */
    fun getLessonsItemInstanceId(indexCategory: Int?, indexLesson: Int?): Int? {
        return courseContentData?.getOrNull(
            indexCategory ?: -1
        )?.modules?.getOrNull(indexLesson ?: -1)?.instance
    }

    /**
     * Получить тестовую попытку
     */
    fun getQuizAttemptContent(): QuizAttempts? {
        return quizAttemptContent
    }

    /**
     * Получить содержимое лекции
     */
    fun getArticleLessonContentItem(): ArticleTaskContentItem? {
        return articleLesson
    }

    /**
     * Получить содержимое элемента теста
     */
    fun getTestTaskContentItem(): QuizTaskContentItem? {
        return quizTaskContentItem
    }

    /**
     * Получить индекс урока
     */
    fun getLessonContentItemIndex(): MutableState<Int> {
        return lessonItemIndex
    }

    /**
     * Получить индекс элемента урока
     */
    fun getTaskContentItemIndexState(): MutableState<Int> {
        return taskItemIndex
    }

    /**
     * Получить id попытки
     */
    fun getAttemptId(): MutableState<Int> {
        return testAttemptId
    }

    /**
     * Получить количество уроков в категории
     */
    fun getLessonContentSize(): Int {
        return courseContentData?.getOrNull(categoryLessonItemIndex.value)?.modules?.size
            ?: 0
    }

    /**
     * Получить количество страниц в лекции
     */
    fun getTaskArticlesContentSize(): Int {
        return lessonPages?.pages?.size ?: 0
    }

    // javaScriptCode для html
    private val jsGetRadioButtonResult: String =
        "function findCheckedRadio() {\n" +
            "  let arrayR0 = document.getElementsByClassName(\"r0\")\n" +
            "  let arrayR1 = document.getElementsByClassName(\"r1\")\n" +
            "  for (let i = 0; i < arrayR0.length; i++) {\n" +
            "    for (const value of arrayR0[i].childNodes.values()) {\n" +
            "      if (value.nodeName === \"INPUT\" && value.checked)\n" +
            "        android.postRadioButtonResult(value.id) // сделать разбиение на qx:x_answer и число после этого\n" +
            "    }\n" +
            "  }\n" +
            "  for (let i = 0; i < arrayR1.length; i++) {\n" +
            "    for (const value of arrayR1[i].childNodes.values()) {\n" +
            "      if (value.nodeName === \"INPUT\" && value.checked)\n" +
            "        android.postRadioButtonResult(value.id) // сделать разбиение на qx:x_answer и число после этого\n" +
            "      // return value.id // сделать разбиение на qx:x_answer и число после этого\n" +
            "    }\n" +
            "  }\n" +
            "  return undefined\n" +
            "}\n"

    private val jsGetCheckBoxResult: String =
        "function findCheckBox() {\n" +
            "  let arrayR0 = document.getElementsByClassName(\"r0\")\n" +
            "  let arrayR1 = document.getElementsByClassName(\"r1\")\n" +
            "  let resArray = []\n" +
            "  for (let i = 0; i < arrayR0.length; i++) {\n" +
            "    for (const value of arrayR0[i].childNodes.values()) {\n" +
            "      if (value.nodeName === \"INPUT\" && value.checked)\n" +
            "        resArray.push(value.id)\n" +
            "    }\n" +
            "  }\n" +
            "  for (let i = 0; i < arrayR1.length; i++) {\n" +
            "    for (const value of arrayR1[i].childNodes.values()) {\n" +
            "      if (value.nodeName === \"INPUT\" && value.checked)\n" +
            "        resArray.push(value.id)\n" +
            "      // return value.id // сделать разбиение на qx:x_answer и число после этого\n" +
            "    }\n" +
            "  }\n" +
            "  let resString = \"\"\n" +
            "  for (let i = 0; i < resArray.length; i++) {\n" +
            "    resString += resArray[i] + \";\"\n" +
            "  }\n" +
            "  android.postCheckBoxResult(resString) // сделать разбиение на qx:x_answer и число после этого\n" +
            "  return undefined\n" +
            "}\n"
    private val jsGetTextAnswerResult: String =
        "function findTextAnswer() {\n" +
            "  let answer = document.getElementsByClassName(\"form-control d-inline\")\n" +
            "  if (answer[0].nodeName === \"INPUT\")\n" +
            "    android.postTextResult(answer[0].id + \";\" + answer[0].value)\n" +
            "  return undefined\n" +
            "}\n"
    private val jsGetNumericalAnswerResult: String =
        "function findNumericalAnswer() {\n" +
            "  let answer = document.getElementsByClassName(\"form-control d-inline\")\n" +
            "  if (answer[0].nodeName === \"INPUT\")\n" +
            "    android.postNumericalResult(answer[0].id + \";\" + answer[0].value)\n" +
            "  return undefined\n" +
            "}\n"
    private val jsGetMatchesResult: String =
        "function findMatches() {\n" +
            "  let answer = document.getElementsByClassName(\"select custom-select custom-select ml-1\")\n" +
            "  let resString = \"\"\n" +
            "  for (let i = 0; i < answer.length; i++) {\n" +
            "    for (let j = 0; j < answer[i].length; j++) {\n" +
            "      if (answer[i].nodeName === \"SELECT\" && answer[i][j].selected)\n" +
            "        resString += answer[i].name + \";\" + j.toString()\n" +
            "    }\n" +
            "    resString += \";\"\n" +
            "  }\n" +
            "  android.postMatchesResult(resString)\n" +
            "  return undefined\n" +
            "}\n"
    private fun addCheckers(): String {
        return "<script>$jsGetRadioButtonResult$jsGetCheckBoxResult$jsGetTextAnswerResult$jsGetNumericalAnswerResult$jsGetMatchesResult</script>"
    }
}

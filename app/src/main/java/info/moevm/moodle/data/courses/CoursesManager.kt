package info.moevm.moodle.data.courses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.ui.coursescreen.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class CoursesManager(
//    private val token: String, -- нужно добавить
    private val moodleApi: MoodleApi,
    private val courseName: String,
    private var courseContentItemIndex: MutableState<Int>,
    private var lessonContentItemIndex: MutableState<Int>,
    private var taskContentItemIndex: MutableState<Int>,
    private var testAttemptKey: MutableState<String>,
) {
    private var courseData: List<CourseContentItem>? = null
    private var coursesLoaded: Boolean = false
    private var articleLessonContent: ArticleContentItems? = null
    private var testLessonContent: TestContentItems? = null

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
            TaskType.TEST ->{
                testLessonContent = lessonContent as TestContentItems
                articleLessonContent = null
            }
            else -> {
            }
        }
    }

    fun receiveFullCoursesData() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                courseData = moodleApi.getFakeCourses(courseName)
                coursesLoaded = true
            }
            withContext(Dispatchers.Main) {
                changeLessonItem()
            }

        }
    }

    fun moveTaskIndex(delta: Int = 1) {
        if (articleLessonContent != null) {
            if (0 <= taskContentItemIndex.value + delta && taskContentItemIndex.value + delta < articleLessonContent?.taskContent?.size ?: 0)
                taskContentItemIndex.value += delta
        } else if (testLessonContent != null) {
            if (0 <= taskContentItemIndex.value + delta && taskContentItemIndex.value + delta < testLessonContent?.taskContent.orEmpty()[testAttemptKey.value]?.second?.size ?: 0)
                taskContentItemIndex.value += delta
        }
    }

    fun setAttemptKey(newTestAttemptKey: String) {
        //TODO
    }

    fun moveLessonIndex(delta: Int) {
        //TODO
    }
    fun setLessonIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseData?.get(courseContentItemIndex.value)?.lessonContent?.size ?: 0) {
            lessonContentItemIndex.value = newIndex
            taskContentItemIndex.value = 0
            changeLessonItem()
        } else
            throw IllegalArgumentException()
    }

    fun moveCourseIndex(delta: Int) {
        //TODO
    }

    fun setCourseIndex(newIndex: Int) {
        if (0 <= newIndex && newIndex < courseData?.size ?: 0) {
            courseContentItemIndex.value = newIndex
            lessonContentItemIndex.value = 0
            taskContentItemIndex.value = 0
            changeLessonItem()
        } else
            throw IllegalArgumentException()
    }


    fun moveTo(
        courseContentItemIndex: Int,
        lessonContentItemIndex: Int,
        taskContentItemIndex: Int
    ) {
        //TODO
    }

    //Getters
    fun getLessonsTitles(): List<String> {
        return courseData?.map { it.lessonTitle }.orEmpty()
    }

    fun getLessonsContents(index: Int? = null): List<LessonContentItem?> {
        return courseData?.get(index ?: courseContentItemIndex.value)?.lessonContent.orEmpty()
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
        return testAttemptKey;
    }

    fun getCoursesContentSize(): Int {
        return courseData?.size ?: 0
    }

    fun getLessonContentSize(): Int {
        return courseData?.get(courseContentItemIndex.value)?.lessonContent?.size ?: 0
    }

    fun getTaskAttemptsCount(): Int {
        return (courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(lessonContentItemIndex.value) as TestContentItems?)?.taskContent?.size ?: 0
    }

    fun getTaskTestsContentSize(): Int {
        return (courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(lessonContentItemIndex.value) as TestContentItems?)?.taskContent?.get(testAttemptKey.value)?.second?.size ?: 0
    }
    fun getTaskArticlesContentSize(): Int {
        return (courseData?.get(courseContentItemIndex.value)?.lessonContent?.get(lessonContentItemIndex.value) as ArticleContentItems?)?.taskContent?.size ?: 0
    }

}

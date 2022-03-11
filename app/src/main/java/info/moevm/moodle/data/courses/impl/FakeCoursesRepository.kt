package info.moevm.moodle.data.courses.impl

import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.data.Result
import info.moevm.moodle.data.courses.CoursesMap
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.courses.AllCourseSelection
import info.moevm.moodle.utils.addOrRemove
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.ArrayList

/**
 * Implementation of InterestRepository that returns a hardcoded list of
 * topics, people and publications synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeCoursesRepository : CoursesRepository {
    private val publications by lazy {
        listOf(
            "Курс молодого бойца",
            "Compose Mix",
            "Compose Breakdown",
            "Android Pursue",
            "Kotlin Watchman",
            "Jetpack Ark",
            "Composeshack",
            "Jetpack Point",
            "Compose Tribune"
        )
    }

    // for now, keep the selections in memory
    private val selectedTopics = MutableStateFlow(setOf<AllCourseSelection>())
    private val selectedPeople = MutableStateFlow(setOf<String>())
    private val selectedPublications = MutableStateFlow(setOf<String>())

    // Used to make suspend functions that read and update state safe to call from any thread
    private val mutex = Mutex()

    override suspend fun getAllCourse(token: String): Result<CoursesMap> {
        if (token.isEmpty())
            Timber.i("token in getTopics empty")
        else
            Timber.i("token in getTopics not empty")

        val apiclient = MoodleApi()
        val data = apiclient.getCourses(token)

        val coursesList = data?.courses?.toMutableList()
        val topicMap: MutableMap<String, List<Pair<String, Int>>> = HashMap<String, List<Pair<String, Int>>>()

        if (coursesList != null) {
            for (i in coursesList) {
                topicMap.put(i.categoryname.toString(), emptyList())
            }
            for (i in topicMap) {
                val topicList: MutableList<Pair<String, Int>> = ArrayList()
                for (j in coursesList) {
                    if (i.key == j.categoryname.toString()) {
                        topicList.add(Pair(j.shortname.toString(), j.id ?: -1))
                    }
                }
                topicMap.put(i.key, topicList)
            }
            return Result.Success(topicMap)
        }
        Timber.d(topicMap.toString())

        return Result.Success(topicMap)
    }

    override suspend fun getCurrentCourse(token: String): Result<List<Pair<String, Int>>> {
        if (token.isEmpty())
            Timber.i("token in getPeople empty")
        else
            Timber.i("token in getPeople not empty")

        val apiclient = MoodleApi()
        val data = apiclient.getCurrentCourses(token)

        val coursesList = data?.courses?.toMutableList()
        val topicList: MutableList<Pair<String, Int>> = ArrayList()

        if (coursesList != null) {
            for (i in coursesList) {
                topicList.add(Pair(i.fullname.toString(), i.id ?: -1))
            }
        }
        Timber.d(topicList.toString())
        return Result.Success(topicList)
    }

    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(publications)
    }

    override suspend fun toggleAllCourseSelection(allCourse: AllCourseSelection) {
        mutex.withLock {
            val set = selectedTopics.value.toMutableSet()
            set.addOrRemove(allCourse)
            selectedTopics.value = set
        }
    }

    override suspend fun toggleCurrentCourseSelected(person: String) {
        mutex.withLock {
            val set = selectedPeople.value.toMutableSet()
            set.addOrRemove(person)
            selectedPeople.value = set
        }
    }

    override suspend fun togglePublicationSelected(publication: String) {
        mutex.withLock {
            val set = selectedPublications.value.toMutableSet()
            set.addOrRemove(publication)
            selectedPublications.value = set
        }
    }

    override fun observeAllCourseSelected(): Flow<Set<AllCourseSelection>> = selectedTopics

    override fun observeCurrentCourseSelected(): Flow<Set<String>> = selectedPeople

    override fun observePublicationSelected(): Flow<Set<String>> = selectedPublications
}

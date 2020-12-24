package info.moevm.moodle.data.courses.impl

import androidx.lifecycle.Transformations.map
import info.moevm.moodle.api.MoodleApi
import info.moevm.moodle.data.Result
import info.moevm.moodle.data.courses.CoursesMap
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.courses.TopicSelection
import info.moevm.moodle.utils.addOrRemove
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.ArrayList

/**
 * Implementation of InterestRepository that returns a hardcoded list of
 * topics, people and publications synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeCoursesRepository : CoursesRepository {

    private val topics by lazy {
        mapOf(
            "Android" to listOf("Jetpack Compose", "Kotlin", "Jetpack"),
            "Programming" to listOf("Kotlin", "Declarative UIs", "Java"),
            "Technology" to listOf("Pixel", "Google")
        )
    }

    private val currentTopics by lazy {
        listOf(
            "Kobalt Toral",
            "K'Kola Uvarek",
            "Kris Vriloc",
            "Grala Valdyr",
            "Kruel Valaxar",
            "L'Elij Venonn",
            "Kraag Solazarn",
            "Tava Targesh",
            "Kemarrin Muuda"
        )
    }

    private val publications by lazy {
        listOf(
            "Kotlin Vibe",
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
    private val selectedTopics = MutableStateFlow(setOf<TopicSelection>())
    private val selectedPeople = MutableStateFlow(setOf<String>())
    private val selectedPublications = MutableStateFlow(setOf<String>())

    // Used to make suspend functions that read and update state safe to call from any thread
    private val mutex = Mutex()

    override suspend fun getTopics(): Result<CoursesMap> {

        val apiclient = MoodleApi()
        // val data: LiveData<CurrentCourses>?
        val data = apiclient.getCourses("bdb63ddf3497ad850020d3482c87fbde")
        System.out.println(data)

        val coursesList = data?.courses?.toMutableList()
        System.out.println(coursesList)
        val topicMap: MutableMap<String, List<String>> = HashMap<String, List<String>>()

        if (coursesList != null) {
            for (i in coursesList) {
                topicMap.put(i.categoryname.toString(), emptyList())
            }
            for( i in topicMap) {
                val topicList: MutableList<String> = ArrayList()
                for (j in coursesList) {
                    if(i.key == j.categoryname.toString()){
                        topicList.add(j.shortname.toString())
                    }
                }
                topicMap.put(i.key, topicList)
            }
            System.out.println(topicMap)
            return Result.Success(topicMap)
        }
        System.out.println(topicMap)

        return Result.Success(topicMap)
    }

    override suspend fun getPeople(): Result<List<String>> {
        val apiclient = MoodleApi()
        // val data: LiveData<CurrentCourses>?
        val data = apiclient.getCurrentCourses("bdb63ddf3497ad850020d3482c87fbde")
        System.out.println(data)

        val coursesList = data?.courses?.toMutableList()
        System.out.println(coursesList)
        val topicList: MutableList<String> = ArrayList()

        if (coursesList != null) {
            for (i in coursesList) {
                topicList.add(i.fullname.toString())
            }
        }
        System.out.println(topicList)
        return Result.Success(topicList)
    }

    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(publications)
    }

    override suspend fun toggleTopicSelection(topic: TopicSelection) {
        mutex.withLock {
            val set = selectedTopics.value.toMutableSet()
            set.addOrRemove(topic)
            selectedTopics.value = set
        }
    }

    override suspend fun togglePersonSelected(person: String) {
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

    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> = selectedTopics

    override fun observePeopleSelected(): Flow<Set<String>> = selectedPeople

    override fun observePublicationSelected(): Flow<Set<String>> = selectedPublications
}

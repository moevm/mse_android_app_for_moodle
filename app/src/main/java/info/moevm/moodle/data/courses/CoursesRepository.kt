package info.moevm.moodle.data.courses

import info.moevm.moodle.data.Result
import kotlinx.coroutines.flow.Flow

typealias CoursesMap = Map<String, List<Pair<String, Int>>>

/**
 * Interface to the Interests data layer.
 */
interface CoursesRepository {

    /**
     * Get relevant topics to the user.
     */
    suspend fun getAllCourse(token: String): Result<CoursesMap>

    /**
     * Get list of people.
     */
    suspend fun getCurrentCourse(token: String): Result<List<Pair<String, Int>>>

    /**
     * Get list of publications.
     */
    suspend fun getPublications(): Result<List<String>>

    /**
     * Toggle between selected and unselected
     */
    suspend fun toggleAllCourseSelection(allCourse: AllCourseSelection)

    /**
     * Toggle between selected and unselected
     */
    suspend fun toggleCurrentCourseSelected(person: String)

    /**
     * Toggle between selected and unselected
     */
    suspend fun togglePublicationSelected(publication: String)

    /**
     * Currently selected topics
     */
    fun observeAllCourseSelected(): Flow<Set<AllCourseSelection>>

    /**
     * Currently selected people
     */
    fun observeCurrentCourseSelected(): Flow<Set<String>>

    /**
     * Currently selected publications
     */
    fun observePublicationSelected(): Flow<Set<String>>
}

data class AllCourseSelection(val section: String, val topic: String)

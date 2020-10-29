package info.moevm.moodle.data

import android.content.Context
import info.moevm.moodle.data.interests.InterestsRepository
import info.moevm.moodle.data.interests.impl.FakeInterestsRepository
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.data.posts.impl.FakePostsRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    /**
     * By default, the calculation of lazy properties is synchronized:
     * the value is calculated only in one execution thread, and all
     * other threads can see the same value
     */
    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository(
            resources = applicationContext.resources
        )
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}

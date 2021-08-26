package info.moevm.moodle.data.posts.impl

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import info.moevm.moodle.data.Result
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.model.Post
import info.moevm.moodle.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockingFakePostsRepository(private val context: Context) : PostsRepository {

    private val postsWithResources: List<Post> by lazy {
        posts.map {
            it.copy(
                image = ImageBitmap.imageResource(context.resources, it.imageId),
                // FIXME: is it necessary?
                imageThumb = ImageBitmap.imageResource(context.resources, it.imageThumbId)
            )
        }
    }

    // for now, keep the favorites in memory
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getPost(postId: String): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = postsWithResources.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Unable to find post"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPosts(): Result<List<Post>> {
        return Result.Success(postsWithResources)
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        set.addOrRemove(postId)
        favorites.value = set
    }
}

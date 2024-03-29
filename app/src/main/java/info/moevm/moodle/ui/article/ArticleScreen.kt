package info.moevm.moodle.ui.article

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.data.Result
import info.moevm.moodle.data.posts.PostsRepository
import info.moevm.moodle.data.posts.impl.BlockingFakePostsRepository
import info.moevm.moodle.data.posts.impl.post3
import info.moevm.moodle.model.Post
import info.moevm.moodle.ui.ThemedPreview
import info.moevm.moodle.ui.home.BookmarkButton
import info.moevm.moodle.utils.produceUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Stateful Article Screen that manages state using [produceUiState]
 *
 * @param postId (state) the post to show
 * @param postsRepository data source for this screen
 * @param onBack (event) request back navigation
 */
@Suppress("DEPRECATION") // allow ViewModelLifecycleScope call
@Composable
fun ArticleScreen(
    postId: String,
    postsRepository: PostsRepository,
    onBack: () -> Unit
) {
    val (post) = produceUiState(postsRepository, postId) {
        getPost(postId)
    }
    // TODO: handle errors when the repository is capable of creating them
    val postData = post.value.data ?: return

    // [collectAsState] will automatically collect a Flow<T> and return a State<T> object that
    // updates whenever the Flow emits a value. Collection is cancelled when [collectAsState] is
    // removed from the composition tree.
    val favorites by postsRepository.observeFavorites().collectAsState(setOf())
    val isFavorite = favorites.contains(postId)

    // Returns a [CoroutineScope] that is scoped to the lifecycle of [ArticleScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope = rememberCoroutineScope()

    ArticleScreen(
        post = postData,
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = {
            coroutineScope.launch { postsRepository.toggleFavorite(postId) }
        }
    )
}

/**
 * Stateless Article Screen that displays a single post.
 *
 * @param post (state) item to display
 * @param onBack (event) request navigate back
 * @param isFavorite (state) is this item currently a favorite
 * @param onToggleFavorite (event) request that this post toggle it's favorite state
 */
@Composable
fun ArticleScreen(
    post: Post,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = MaterialTheme.typography.subtitle2,
                        color = LocalContentColor.current
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                post = post,
                onUnimplementedAction = { showDialog = true },
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        PostContent(post, modifier)
    }
}

/**
 * Bottom bar for Article screen
 *
 * @param post (state) used in share sheet to share the post
 * @param onUnimplementedAction (event) called when the user performs an unimplemented action
 * @param isFavorite (state) if this post is currently a favorite
 * @param onToggleFavorite (event) request this post toggle it's favorite status
 */
@Composable
private fun BottomBar(
    post: Post,
    onUnimplementedAction: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Surface(elevation = 2.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(Icons.Filled.FavoriteBorder, null)
            }
            BookmarkButton(
                isBookmarked = isFavorite,
                onClick = onToggleFavorite
            )
            val context = LocalContext.current
            IconButton(onClick = { sharePost(post, context) }) {
                Icon(Icons.Filled.Share, null)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onUnimplementedAction) {
                Icon(ImageVector.vectorResource(R.drawable.ic_text_settings), null)
            }
        }
    }
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(R.string.function_not_available_info),
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close_info))
            }
        }
    )
}

/**
 * Show a share sheet for a post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 */
private fun sharePost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_post)))
}

@Preview("Article screen")
@Composable
fun PreviewArticle() {
    ThemedPreview {
        val post = loadFakePost(post3.id)
        ArticleScreen(post, {}, false, {})
    }
}

@Preview("Article screen dark theme")
@Composable
fun PreviewArticleDark() {
    ThemedPreview(darkTheme = true) {
        val post = loadFakePost(post3.id)
        ArticleScreen(post, {}, false, {})
    }
}

@Composable
private fun loadFakePost(postId: String): Post {
    val context = LocalContext.current
    val post = runBlocking {
        (BlockingFakePostsRepository(context).getPost(postId) as Result.Success).data
    }
    return post
}

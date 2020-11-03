package info.moevm.moodle.ui.home

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import info.moevm.moodle.R
import info.moevm.moodle.data.posts.impl.post3
import info.moevm.moodle.model.Post
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.ThemedPreview

@Composable
fun AuthorAndReadTime(
    post: Post,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        ProvideEmphasis(AmbientEmphasisLevels.current.medium) {
            val textStyle = MaterialTheme.typography.body2
            Text(
                text = post.metadata.author.name,
                style = textStyle
            )
            Text(
                text = " - ${post.metadata.hoursToPass}h to pass",
                style = textStyle
            )
        }
    }
}

@Composable
fun PostImage(post: Post, modifier: Modifier = Modifier) {
    val image = post.imageThumb ?: imageResource(R.drawable.placeholder_1_1)
    Image(
        asset = image,
        modifier = modifier
            .preferredSize(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun PostTitle(post: Post) {
    ProvideEmphasis(AmbientEmphasisLevels.current.high) {
        Text(post.title, style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun PostCardSimple(
    post: Post,
    navigateTo: (Screen) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
            .padding(16.dp)
    ) {
        PostImage(post, Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            PostTitle(post)
            AuthorAndReadTime(post)
        }
        BookmarkButton(
            isBookmarked = isFavorite,
            onClick = onToggleFavorite
        )
    }
}

@Composable
fun PostCardHistory(post: Post, navigateTo: (Screen) -> Unit) {
    Row(
        Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
            .padding(16.dp)
    ) {
        PostImage(
            post = post,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(Modifier.weight(1f)) {
            ProvideEmphasis(AmbientEmphasisLevels.current.medium) {
                Text(
                    text = "Open course",
                    style = MaterialTheme.typography.overline
                )
            }
            PostTitle(post = post)
            AuthorAndReadTime(
                post = post,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        ProvideEmphasis(AmbientEmphasisLevels.current.medium) {
            Icon(asset = Icons.Filled.MoreVert)
        }
    }
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() }
    ) {
        modifier.fillMaxSize()
        if (isBookmarked) {
            Icon(
                asset = Icons.Filled.Bookmark,
                modifier = modifier
            )
        } else {
            Icon(
                asset = Icons.Filled.BookmarkBorder,
                modifier = modifier
            )
        }
    }
}

@Preview("Bookmark Button")
@Composable
fun BookmarkButtonPreview() {
    ThemedPreview {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
fun BookmarkButtonBookmarkedPreview() {
    ThemedPreview {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

@Preview("Simple post card")
@Composable
fun SimplePostPreview() {
    ThemedPreview {
        PostCardSimple(post3, {}, false, {})
    }
}

@Preview("Post History card")
@Composable
fun HistoryPostPreview() {
    ThemedPreview {
        PostCardHistory(post3, {})
    }
}

@Preview("Simple post card dark theme")
@Composable
fun SimplePostDarkPreview() {
    ThemedPreview(darkTheme = true) {
        PostCardSimple(post3, {}, false, {})
    }
}
package info.moevm.moodle.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import info.moevm.moodle.data.posts.impl.getPostsWithImagesLoaded
import info.moevm.moodle.data.posts.impl.post2
import info.moevm.moodle.data.posts.impl.posts
import info.moevm.moodle.model.Post
import info.moevm.moodle.ui.ThemedPreview

@Composable
fun PostCardTop(post: Post, modifier: Modifier = Modifier) {
    // TUTORIAL CONTENT STARTS HERE
    val typography = MaterialTheme.typography
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        post.image?.let { image ->
            val imageModifier = Modifier
                .heightIn(min = 180.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
            Image(image, modifier = imageModifier, contentDescription = null, contentScale = ContentScale.Crop)
        }
        Spacer(Modifier.height(16.dp))

        val emphasisLevels = LocalContentAlpha.current
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = post.title,
                style = typography.h6
            )
            Text(
                text = post.metadata.author.name,
                style = typography.body2
            )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = "${post.metadata.date} - ${post.metadata.hoursToPass}h to pass",
                style = typography.body2
            )
        }
    }
}
// TUTORIAL CONTENT ENDS HERE

// Preview section

@Preview("Default colors")
@Composable
fun TutorialPreview() {
    TutorialPreviewTemplate()
}

@Preview("Dark theme")
@Composable
fun TutorialPreviewDark() {
    TutorialPreviewTemplate(darkTheme = true)
}

@Preview("Font scaling 1.5", fontScale = 1.5f)
@Composable
fun TutorialPreviewFontscale() {
    TutorialPreviewTemplate()
}

@Composable
fun TutorialPreviewTemplate(
    darkTheme: Boolean = false
) {
    val context = LocalContext.current
    val previewPosts = getPostsWithImagesLoaded(posts.subList(1, 2), context.resources)
    val post = previewPosts[0]

    ThemedPreview(darkTheme) {
        PostCardTop(post)
    }
}

@Preview("Post card top")
@Composable
fun PreviewPostCardTop() {
    ThemedPreview {
        PostCardTop(post = post2)
    }
}

@Preview("Post card top dark theme")
@Composable
fun PreviewPostCardTopDark() {
    ThemedPreview(darkTheme = true) {
        PostCardTop(post = post2)
    }
}

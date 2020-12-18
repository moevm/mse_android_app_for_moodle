package info.moevm.moodle.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.data.posts.impl.post1
import info.moevm.moodle.model.Post
import info.moevm.moodle.model.PostAuthor
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.ThemedPreview

@Composable
fun PostCardPopular(
    post: Post,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.preferredSize(280.dp, 240.dp)
    ) {
        Column(modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })) {
            val image = post.image ?: imageResource(R.drawable.placeholder_4_3)
            Image(
                bitmap = image,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .preferredHeight(100.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                val emphasisLevels = AmbientContentAlpha.current
                Providers(AmbientContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.h6,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = post.metadata.author.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2
                    )
                }
                Providers(
                    AmbientContentAlpha provides ContentAlpha.high
                ) {
                    Text(
                        text = "${post.metadata.date} - " +
                            "${post.metadata.hoursToPass}h to pass",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Preview("Regular colors")
@Composable
fun PreviewPostCardPopular() {
    ThemedPreview {
        PostCardPopular(post1, {})
    }
}

@Preview("Dark colors")
@Composable
fun PreviewPostCardPopularDark() {
    ThemedPreview(darkTheme = true) {
        PostCardPopular(post1, {})
    }
}

@Preview("Regular colors, long text")
@Composable
fun PreviewPostCardPopularLongText() {
    val loremIpsum =
        """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras ullamcorper pharetra massa,
        sed suscipit nunc mollis in. Sed tincidunt orci lacus, vel ullamcorper nibh congue quis.
        Etiam imperdiet facilisis ligula id facilisis. Suspendisse potenti. Cras vehicula neque sed
        nulla auctor scelerisque. Vestibulum at congue risus, vel aliquet eros. In arcu mauris,
        facilisis eget magna quis, rhoncus volutpat mi. Phasellus vel sollicitudin quam, eu
        consectetur dolor. Proin lobortis venenatis sem, in vestibulum est. Duis ac nibh interdum,
        """.trimIndent()
    ThemedPreview {
        PostCardPopular(
            post1.copy(
                title = "Title$loremIpsum",
                metadata = post1.metadata.copy(
                    author = PostAuthor("Author: $loremIpsum"),
                    hoursToPass = Int.MAX_VALUE
                )
            ),
            {}
        )
    }
}

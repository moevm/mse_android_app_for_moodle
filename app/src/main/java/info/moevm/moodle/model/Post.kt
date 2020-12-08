package info.moevm.moodle.model

import androidx.compose.ui.graphics.ImageAsset

data class Post(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val url: String,
    val publication: Publication? = null,
    val metadata: Metadata,
    val paragraphs: List<Paragraph> = emptyList(),
    val imageId: Int,
    val imageThumbId: Int,
    val image: ImageAsset? = null,
    val imageThumb: ImageAsset? = null
)

data class Metadata(
    val author: PostAuthor,
    val date: String,
    val hoursToPass: Int
)

data class PostAuthor(
    val name: String,
    val url: String? = null
)

data class Publication(
    val name: String,
    val logoUrl: String
)

data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

data class Status(
    val sentCount: Int,
    val verified: Boolean
)

data class LoginSuccess(
    val token: String,
    val privatetoken:String,
    val error: String,
    val errorcode: String,
    val stacktrace: String,
    val debuginfo: String,
    val reproductionlink: String
)

data class RandomCatFacts(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val deleted: Boolean,
    val source: String,
    val status: Status,
    val text: String,
    val type: String,
    val updatedAt: String,
    val used: Boolean,
    val user: String
)

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}

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
    val token: String? = null,
    val privatetoken: String? = null,
    val error: String? = null,
    val errorcode: String? = null,
    val stacktrace: String? = null,
    val debuginfo: String? = null,
    val reproductionlink: String? = null
)

data class Course(
    val id: Int,
    val fullname: String,
    val displayname: String,
    val shortname: String,
    val categoryid: Int,
    val categoryname: String,
    val sortorder: Int,
    val summary: String,
    val summaryformat: Int,
    val summaryfiles: Array<String>,
    val overviewfiles: Array<String>,
    val contacts: Array<String>,
    val enrollmentmethods: Array<String>,
    val format: String,
    val showgrades: Int,
    val newsitems: Int,
    val startdate: Int,
    val enddate: Int,
    val maxbytes: Int,
    val showreports: Int,
    val visible: Int,
    val groupmode: Int,
    val groupmodeforce: Int,
    val defaultgroupingid: Int,
    val enablecompletion: Int,
    val completionnotify: Int,
    val lang: String,
    val theme: String,
    val marker: Int
)
data class CurrentCourse(
    val id: Int,
    val fullname: String,
    val shortname: String,
    val timemodified: Int,
    val assignments: Array<String>
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

enum class APIVariables(var value: String) {
    MOODLE_MOBILE_APP("moodle_mobile_app"),
    MOODLE_URL("http://e.moevm.info")
// const val MOODLE = "https://10.0.2.2:1010"   need for local server
}

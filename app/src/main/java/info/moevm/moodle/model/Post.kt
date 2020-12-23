package info.moevm.moodle.model

import androidx.compose.ui.graphics.ImageBitmap

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
    val image: ImageBitmap? = null,
    val imageThumb: ImageBitmap? = null
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

data class  File
(
    val filename: String? = null,
    val filepath: String? = null,
    val filesize: Int? = null,
    val fileurl: String? = null,
    val timemodified: Int? = null,
    val mimetype: String? = null,
    val isexternalfile: Int? = null,
    val repositorytype: String? = null
)

data class Contact(
    val id: Int? = null,
    val fullname: String? = null
)

data class CustomField(
    val name: String? = null,
    val shortname: String? = null,
    val type: String? = null,
    val value: String? = null
)

data class Filter(
    val filter: String? = null,
    val localstate: Int? = null,
    val inheritedstate: Int? = null
)

data class CourseFormatOption(
    val name: String? = null,
    val value: String? = null,

)

data class Warning(
    val item: String? = null,
    val itemid: Int? = null,
    val warningcode: String? = null,
    val message: String? = null

)

data class Config(
    val id: Int? = null,
    val assignment: Int? = null,
    val plugin: String? = null,
    val subtype: String? = null,
    val name: String? = null,
    val value: String? = null,
)

data class Assigment(
    val id: Int? = null,
    val cmid: Int? = null,
    val course: Int? = null,
    val name: String? = null,
    val nosubmissions: Int? = null,
    val submissiondrafts: Int? = null,
    val sendnotifications: Int? = null,
    val sendlatenotifications: Int? = null,
    val sendstudentnotifications: Int? = null,
    val duedate: Int? = null,
    val allowsubmissionsfromdate: Int? = null,
    val grade: Int? = null,
    val timemodified: Int? = null,
    val completionsubmit: Int? = null,
    val cutoffdate: Int? = null,
    val gradingduedate: Int? = null,
    val teamsubmission: Int? = null,
    val requireallteammemberssubmit: Int? = null,
    val teamsubmissiongroupingid: Int? = null,
    val blindmarking: Int? = null,
    val hidegrader: Int? = null,
    val revealidentities: Int? = null,
    val attemptreopenmethod: String? = null,
    val maxattempts: Int? = null,
    val markingworkflow: Int? = null,
    val markingallocation: Int? = null,
    val requiresubmissionstatement: Int? = null,
    val preventsubmissionnotingroup: Int? = null,
    val submissionstatement: String? = null,
    val submissionstatementformat: Int? = null,
    val configs: Array<Config>? = null,
    val intro: String? = null,
    val introformat: Int? = null,
    val introfiles: Array<File>? = null,
    val introattachments: Array<File>? = null

)

data class Course(
    val id: Int? = null,
    val fullname: String? = null,
    val displayname: String? = null,
    val shortname: String? = null,
    val categoryid: Int? = null,
    val categoryname: String? = null,
    val sortorder: Int? = null,
    val summary: String? = null,
    val summaryformat: Int? = null,
    val summaryfiles: Array<File>? = null,
    val overviewfiles: Array<File>? = null,
    val contacts: Array<Contact>? = null,
    val enrollmentmethods: Array<String>? = null,
    val customfields: Array<CustomField>? = null,
    val format: String? = null,
    val showgrades: Int? = null,
    val newsitems: Int? = null,
    val startdate: Int? = null,
    val enddate: Int? = null,
    val maxbytes: Int? = null,
    val showreports: Int? = null,
    val visible: Int? = null,
    val groupmode: Int? = null,
    val groupmodeforce: Int? = null,
    val defaultgroupingid: Int? = null,
    val enablecompletion: Int? = null,
    val completionnotify: Int? = null,
    val lang: String? = null,
    val theme: String? = null,
    val marker: Int? = null,
    val legacyfiles: Int? = null,
    val calendartype: String? = null,
    val timecreated: Int? = null,
    val timemodified: Int? = null,
    val requested: Int? = null,
    val cacherev: Int? = null,
    val filters: Array<Filter>? = null,
    val courseformatoptions: Array<CourseFormatOption>? = null


)

data class Courses(
    val courses: Array<Course>? = null,
    val warnings: Array<Warning>? = null
)

data class CurrentCourse(
    val id: Int? = null,
    val fullname: String? = null,
    val shortname: String? = null,
    val timemodified: Int? = null,
    val assignments: Array<Assigment>? = null
)

data class CurrentCourses(
    val courses: Array<CurrentCourse>? = null,
    val warnings: Array<Warning>? = null
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
    MOODLE_WS_REST_FORMAT("json"),
    MOODLE_URL("http://e.moevm.info"),
// const val MOODLE = "https://10.0.2.2:1010"   need for local server
    MOD_ASSIGN_GET_ASSIGMENTS("mod_assign_get_assignments"),
    CORE_COURSE_GET_COURSES_BY_FIELD("core_course_get_courses_by_field")
}

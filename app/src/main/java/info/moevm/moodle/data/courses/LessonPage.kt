package info.moevm.moodle.data.courses

import info.moevm.moodle.model.Warning

data class LessonPage(
    val id: Int?,
    val lessonid: Int?,
    val prevpageid: Int?,
    val nextpageid: Int?,
    val qtype: Int?,
    val qoption: Int?,
    val layout: Int?,
    val display: Int?,
    val timecreated: Int?,
    val timemodified: Int?,
    val title: String?,
    val contents: String?,
    val contentsformat: Int?,
    val displayinmenublock: Boolean?,
    val type: Int?,
    val typeid: Int?,
    val typestring: String?
)
/**
object {
page : LessonPage?                                Page fields
answerids : Array<Int>?                           List of answers ids (empty for content pages in
jumps : Array<Int>?                               List of possible page jumps
filescount : Int?                                 The total number of files attached to the page
filessizetotal : Int?                             The total size of the files

 */
data class LessonPagesData(
    val page: LessonPage?,
    val answerids: Array<Int>?,
    val jumps: Array<Int>?,
    val filescount: Int?,
    val filessizetotal: Int?
)

data class LessonPages(
    val pages: Array<LessonPagesData>?,
    val warnings: Array<Warning>?
)

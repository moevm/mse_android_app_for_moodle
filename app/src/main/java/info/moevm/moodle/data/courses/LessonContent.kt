package info.moevm.moodle.data.courses

data class LessonContent(
    val lesson: Lesson?,
    val warnings: List<WarningItem>?
)

/**
id : Int?                                                      Standard Moodle primary key.
course : Int?                                                  Foreign key reference to the course this lesson is part of.
coursemodule : Int?                                            Course module id.
name : String?                                                 Lesson name.
intro : String?                                 Необязательно  Lesson introduction text.
introformat : Int?                                             По умолчанию - «1» //intro format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
practice : Int?                                 Необязательно  Practice lesson?
modattempts : Int?                              Необязательно  Allow student review?
usepassword : Int?                              Необязательно  Password protected lesson?
password : String?                              Необязательно  Password
dependency : Int?                               Необязательно  Dependent on (another lesson id)
conditions : String?                            Необязательно  Conditions to enable the lesson
grade : Int?                                    Необязательно  The total that the grade is scaled to be out of
custom : Int?                                   Необязательно  Custom scoring?
ongoing : Int?                                  Необязательно  Display ongoing score?
usemaxgrade : Int?                              Необязательно  How to calculate the final grade
maxanswers : Int?                               Необязательно  Maximum answers per page
maxattempts : Int?                              Необязательно  Maximum attempts
review : Int?                                   Необязательно  Provide option to try a question again
nextpagedefault : Int?                          Необязательно  Action for a correct answer
feedback : Int?                                 Необязательно  Display default feedback
minquestions : Int?                             Необязательно  Minimum number of questions
maxpages : Int?                                 Необязательно  Number of pages to show
timelimit : Int?                                Необязательно  Time limit
retake : Int?                                   Необязательно  Re-takes allowed
activitylink : Int?                             Необязательно  Id of the next activity to be linked once the lesson is completed
mediafile : String?                             Необязательно  Local file path or full external URL
mediaheight : Int?                              Необязательно  Popup for media file height
mediawidth : Int?                               Необязательно  Popup for media with
mediaclose : Int?                               Необязательно  Display a close button in the popup?
slideshow : Int?                                Необязательно  Display lesson as slideshow
width : Int?                                    Необязательно  Slideshow width
height : Int?                                   Необязательно  Slideshow height
bgcolor : String?                               Необязательно  Slideshow bgcolor
displayleft : Int?                              Необязательно  Display left pages menu?
displayleftif : Int?                            Необязательно  Minimum grade to display menu
progressbar : Int?                              Необязательно  Display progress bar?
available : Int?                                Необязательно  Available from
deadline : Int?                                 Необязательно  Available until
timemodified : Int?                             Необязательно  Last time settings were updated
completionendreached : Int?                     Необязательно  Require end reached for completion?
completiontimespent : Int?                      Необязательно  Student must do this activity at least for
allowofflineattempts : Int?                                    Whether to allow the lesson to be attempted offline in the mobile app
introfiles : List<IntroFiles>?                  Необязательно  introfiles
mediafiles : List<MediaFiles>?                  Необязательно  mediafiles
 */
data class Lesson(
    val id: Int?,
    val course: Int?,
    val coursemodule: Int?,
    val name: String?,
    val intro: String?,
    val introformat: Int?,
    val practice: Int?,
    val modattempts: Int?,
    val usepassword: Int?,
    val grade: Int?,
    val custom: Int?,
    val ongoing: Int?,
    val usemaxgrade: Int?,
    val maxanswers: Int?,
    val maxattempts: Int?,
    val review: Int?,
    val nextpagedefault: Int?,
    val feedback: Int?,
    val minquestions: Int?,
    val maxpages: Int?,
    val timelimit: Int?,
    val retake: Int?,
    val mediafile: String?,
    val mediaheight: Int?,
    val mediawidth: Int?,
    val mediaclose: Int?,
    val slideshow: Int?,
    val width: Int?,
    val height: Int?,
    val bgcolor: String?,
    val displayleft: Int?,
    val displayleftif: Int?,
    val progressbar: Int?,
    val allowofflineattempts: Int?,
    val introfiles: List<IntroFiles>?,
    val mediafiles: List<MediaFiles>?,
)

/**
filename: String?           Необязательно  File name.
filepath: String?           Необязательно  File path.
filesize: Int?              Необязательно  File size.
fileurl: String?            Необязательно  Downloadable file url.
timemodified: Int?          Необязательно  Time modified.
mimetype: String?           Необязательно  File mime type.
isexternalfile: Int?        Необязательно  Whether is an external file.
repositorytype: String?     Необязательно  The repository type for the external files.
 */
data class IntroFiles( // дубликат Files в Post.kt
    val filename: String?,
    val filepath: String?,
    val filesize: Int?,
    val fileurl: String?,
    val timemodified: Int?,
    val mimetype: String?,
    val isexternalfile: Int?,
    val repositorytype: String?
)

/**
filename : String?          Необязательно  File name.
filepath : String?          Необязательно  File path.
filesize : Int?             Необязательно  File size.
fileurl : String?           Необязательно  Downloadable file url.
timemodified : Int?         Необязательно  Time modified.
mimetype : String?          Необязательно  File mime type.
isexternalfile : Int?       Необязательно  Whether is an external file.
repositorytype : String?    Необязательно  The repository type for the external files.
 */
data class MediaFiles(
    val filename: String?,
    val filepath: String?,
    val filesize: Int?,
    val fileurl: String?,
    val timemodified: Int?,
    val mimetype: String?,
    val isexternalfile: Int?,
    val repositorytype: String?

)

/**
item : String?                         Необязательно   item
itemid : Int?                          Необязательно   item id
warningcode : String?                                  the warning code can be used by the client app to implement specific behaviour
message : String?                                      untranslated english message to explain the warning
 */
data class WarningItem(
    val item: String?,
    val itemid: Int?,
    val warningcode: String?,
    val message: String?
)

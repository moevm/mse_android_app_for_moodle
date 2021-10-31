package info.moevm.moodle.data.courses


/**
id : Int?                                               Section ID
name : String?                                          Section name
visible : Int?                           Необязательно  is the section visible
summary : String?                                       Section description
summaryformat : Int?                                    summary format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
section : Int?                           Необязательно  Section number inside the course
hiddenbynumsections : Int?               Необязательно  Whether is a section hidden in the course format
uservisible : Boolean?                   Необязательно  Is the section visible for the user?
availabilityinfo : String?               Необязательно  Availability information.
modules : Array<CourseModule>?                           Array of module
 */
data class CourseMoodleContentData(
    val id: Int? = null,
    val name: String? = null,
    val visible: Int? = null,
    val summary: String? = null,
    val summaryformat: Int? = null,
    val section: Int? = null,
    val hiddenbynumsections: Int? = null,
    val uservisible: Boolean? = null,
    val availabilityinfo: String? = null,
    val modules: Array<CourseModule>? = null
)


/**
id : Int?                                                              activity id
url : String?                                           Необязательно  activity url
name : String?                                                         activity module name
instance : Int?                                         Необязательно  instance id
description : String?                                   Необязательно  activity description
visible : Int?                                          Необязательно  is the module visible
uservisible : Boolean?                                  Необязательно  Is the module visible for the user?
availabilityinfo : String?                              Необязательно  Availability information.
visibleoncoursepage : Int?                              Необязательно  is the module visible on course page
modicon : String?                                                      activity icon url
modname : String?                                                      activity module type
modplural : String?                                                    activity module plural name
availability : String?                                  Необязательно  module availability settings
indent : Int?                                                          number of identation in the site
onclick : String?                                       Необязательно  Onclick action.
afterlink : String?                                     Необязательно  After link info to be displayed.
customdata : String?                                    Необязательно  Custom data (JSON encoded).
noviewlink : Boolean?                                   Необязательно  Whether the module has no view page
completion : Int?                                       Необязательно  Type of completion tracking: 0 means none, 1 manual, 2 automatic.
completiondata : CourseModuleCompletiondata?            Необязательно  Module completion data.
contents : Array<CourseModuleContent>?
contentsinfo : Array<CourseModuleContentInfo>?          Необязательно  Contents summary information.
 */
data class CourseModule(
    val id: Int? = null,
    val url: String? = null,
    val name: String? = null,
    val instance: Int? = null,
    val description: String? = null,
    val visible: Int? = null,
    val uservisible: Boolean? = null,
    val availabilityinfo: String? = null,
    val visibleoncoursepage: Int? = null,
    val modicon: String? = null,
    val modname: String? = null,
    val modplural: String? = null,
    val availability: String? = null,
    val indent: Int? = null,
    val onclick: String? = null,
    val afterlink: String? = null,
    val customdata: String? = null,
    val noviewlink: Boolean? = null,
    val completion: Int? = null,
    val completiondata: CourseModuleCompletionData? = null,
    val contents: Array<CourseModuleContent>? = null,
    val contentsinfo: Array<CourseModuleContentInfo>? = null
)


/**
state : Int?                            Completion state value: 0 means incomplete, 1 complete, 2 complete pass, 3 complete fail
timecompleted : Int?                    Timestamp for completion status.
overrideby : Int?                       The user id who has overridden the status.
valueused : Boolean?     Необязательно  Whether the completion status affects the availability of another activity.
 */
data class CourseModuleCompletionData(
    val state: Int? = null,
    val timecompleted: Int? = null,
    val overrideby: Int? = null,
    val valueused: Boolean? = null
)


/**
id : Int?,                               Tag id.
name : String?,                          Tag name.
rawname : String?,                       The raw, unnormalised name for the tag as entered by users.
isstandard : Int?,                       Whether this tag is standard.
tagcollid : Int?,                        Tag collection id.
taginstanceid : Int?,                    Tag instance id.
taginstancecontextid : Int?              Context the tag instance belongs to.
itemid : Int?,                           Id of the record tagged.
ordering : Int?,                         Tag ordering.
flag : Int?                              Whether the tag is flagged as inappropriate.
 */
data class CourseModuleContentTagsData(
    val id: Int? = null,
    val name: String? = null,
    val rawname: String? = null,
    val isstandard: Int? = null,
    val tagcollid: Int? = null,
    val taginstanceid: Int? = null,
    val taginstancecontextid: Int? = null,
    val itemid: Int? = null,
    val ordering: Int? = null,
    val flag: Int? = null
)


/**
type : String?                                         a file or a folder or external link
filename : String?                                     filename
filepath : String?                                     filepath
filesize: Int?,                                        filesize
fileurl : String?                       Необязательно  downloadable file url
content : String?                       Необязательно  Raw content, will be used when type is content
timecreated : Int?,                                    Time created
timemodified : Int?,                                   Time modified
sortorder : Int?,                                      Content sort order
mimetype : String?                      Необязательно  File mime type.
isexternalfile : Int?,                  Необязательно  Whether is an external file.
repositorytype : String?                Необязательно  The repository type for external files.
userid : Int?,                                         User who added this content to moodle
author : String?                                       Content owner
license : String?                                      Content license
tags : Array<CourseModuleItemTagsData>? Необязательно  Tags
 */
data class CourseModuleContent(
    val type: String? = null,
    val filename: String? = null,
    val filepath: String? = null,
    val filesize: Int? = null,
    val fileurl: String? = null,
    val content: String? = null,
    val timecreated: Int? = null,
    val timemodified: Int? = null,
    val sortorder: Int? = null,
    val mimetype: String? = null,
    val isexternalfile: Int? = null,
    val repositorytype: String? = null,
    val userid: Int? = null,
    val author: String? = null,
    val license: String? = null,
    val tags: Array<CourseModuleContentTagsData>? = null
)


/**
filescount : Int?,                         Total number of files.
filessize : Int?,                          Total files size.
lastmodified : Int?,                       Last time files were modified.
mimetypes : Array<String>?                 Files mime types.
repositorytype : String?    Необязательно  The repository type for the main file.
 */
data class CourseModuleContentInfo(
    val filescount: Int? = null,
    val filessize: Int? = null,
    val lastmodified: Int? = null,
    val mimetypes: Array<String>? = null,
    val repositorytype: String? = null
)

package info.moevm.moodle.model

/**
 * reference: https://saylordotorg.github.io/api-docs/#functionscore_user_get_users_by_field
 *
 * id int                                 ID of the user
 * username string        Необязательно   The username
 * firstname string       Необязательно   The first name(s) of the user
 * lastname string        Необязательно   The family name of the user
 * fullname string                        The fullname of the user
 * email string           Необязательно   An email address - allow email as root@localhost
 * address string         Необязательно   Postal address
 * phone1 string          Необязательно   Phone 1
 * phone2 string          Необязательно   Phone 2
 * icq string             Необязательно   icq number
 * skype string           Необязательно   skype id
 * yahoo string           Необязательно   yahoo id
 * aim string             Необязательно   aim id
 * msn string             Необязательно   msn number
 * department string      Необязательно   department
 * institution string     Необязательно   institution
 * idnumber string        Необязательно   An arbitrary ID code number perhaps from the institution
 * interests string       Необязательно   user interests (separated by commas)
 * firstaccess int        Необязательно   first access to the site (0 if never)
 * lastaccess int         Необязательно   last access to the site (0 if never)
 * auth string            Необязательно   Auth plugins include manual, ldap, etc
 * suspended int          Необязательно   Suspend user account, either false to enable user login or true to disable it
 * confirmed int          Необязательно   Active user: 1 if confirmed, 0 otherwise
 * lang string            Необязательно   Language code such as "en", must exist on server
 * calendartype string    Необязательно   Calendar type such as "gregorian", must exist on server
 * theme string           Необязательно   Theme name such as "standard", must exist on server
 * timezone string        Необязательно   Timezone code such as Australia/Perth, or 99 for default
 * mailformat int         Необязательно   Mail format code is 0 for plain text, 1 for HTML etc
 * description string     Необязательно   User profile description
 * descriptionformat int  Необязательно   int format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
 * city string            Необязательно   Home city of the user
 * url string             Необязательно   URL of the user
 * country string         Необязательно   Home country code of the user, such as AU or CZ
 * profileimageurlsmall string            User image profile URL - small version
 * profileimageurl string                 User image profile URL - big version
 * customfields           Необязательно   User custom fields (also known as user profile fields)
 *
 * ...
 */
data class MoodleUser(
    var id: Int,
    var fullname: String,
    var profileimageurl: String
)

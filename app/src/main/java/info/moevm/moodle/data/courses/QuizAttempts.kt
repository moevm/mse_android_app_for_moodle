package info.moevm.moodle.data.courses

/**
val id: Int?,                              Необязательно //Attempt id.
val quiz: Int?,                            Необязательно //Foreign key reference to the quiz that was attempted.
val userid: Int?,                          Необязательно //Foreign key reference to the user whose attempt this is.
val attempt: Int?,                         Необязательно //Sequentially numbers this students attempts at this quiz.
val uniqueid: Int?,                        Необязательно //Foreign key reference to the question_usage that holds the details of the the question_attempts that make up this quiz attempt.
val layout: String?,                       Необязательно //Attempt layout.
val currentpage: Int?,                     Необязательно //Attempt current page.
val preview: Int?,                         Необязательно //Whether is a preview attempt or not.
val state: String?,                        Необязательно //The current state of the attempts. 'inprogress', 'overdue', 'finished' or 'abandoned'.
val timestart: Int?,                       Необязательно //Time when the attempt was started.
val timefinish: Int?,                      Необязательно //Time when the attempt was submitted. 0 if the attempt has not been submitted yet.
val timemodified: Int?,                    Необязательно //Last modified time.
val timemodifiedoffline: Int?,             Необязательно //Last modified time via webservices.
val timecheckstate: Int?,                  Необязательно //Next time quiz cron should check attempt for state changes. NULL means never check.
val sumgrades: Int?                        Необязательно //Total marks for this attempt.
 */
data class Attempt(
    val id: Int? = null,
    val quiz: Int? = null,
//    val userid: Int? = null,
//    val attempt: Int? = null,
//    val uniqueid: Int? = null,
//    val layout: String? = null,
//    val currentpage: Int? = null,
//    val preview: Int? = null,
    val state: String? = null,
//    val timestart: Int? = null,
    val timefinish: Int? = null,
//    val timemodified: Int? = null,
//    val timemodifiedoffline: Int? = null,
//    val timecheckstate: Int? = null,
//    val sumgrades: Double? = null
)

data class QuizAttempts(
    val attempts: MutableList<Attempt?>? = null,
    val warnings: List<WarningItem>? = null
)

data class QuizAttempt(
    val attempt: Attempt? = null,
    val warnings: List<WarningItem>? = null
)

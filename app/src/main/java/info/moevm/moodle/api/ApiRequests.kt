package info.moevm.moodle.api

import info.moevm.moodle.data.courses.*
import info.moevm.moodle.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiRequests {
    @GET("/login/token.php")
    fun logIn(@Query("service") serviceName: String, @Query("username") userName: String, @Query("password") passWord: String): Call<LoginSuccess>

    @GET("/webservice/rest/server.php")
    fun getCourses(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<Courses>

    @GET("/webservice/rest/server.php")
    fun getCurrentCourses(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<CurrentCourses>

    @GET("/webservice/rest/server.php")
    fun checkTokenLife(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<WrongToken>

    /**
     * Retrieve users' information for a specified unique field, in this case - email
     */
    @GET("/webservice/rest/server.php")
    fun getUserInformation(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("moodlewsrestformat") moodlewsRestFormat: String, @Query("field") username: String, @Query("values[0]") userLogin: String): Call<List<MoodleUser>>

    @GET("/webservice/rest/server.php")
    fun getCourseContent(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("courseid") courseId: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<List<CourseMoodleContentData>?>

    /**
     * Get lectures content
     */

    @GET("/webservice/rest/server.php")
    fun getLessonPages(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("lessonid") lessonId: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<LessonPages?>

//    @GET("/webservice/rest/server.php")
//    fun getLessonPage(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("lessonid") lessonId: String, @Query("pageid") pageId: String, @Query("moodlewsrestformat") moodlewsRestFormat: String)

    /**
     * Get tests content
     */

    @GET("/webservice/rest/server.php")
    fun getQuizAttempts(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("quizid") quizid: String, @Query("status") status: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<QuizAttempts?>

    @GET("/webservice/rest/server.php")
    fun startNewAttempt(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("quizid") quizid: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<QuizAttempt?>

    @GET("/webservice/rest/server.php")
    fun getQuizInProgress(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("attemptid") attemptid: String, @Query("page") page: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<QuizInProgress?>

    @GET("/webservice/rest/server.php")
    fun requireQuizSaveAttempt(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("attemptid") attemptid: String, @QueryMap answer: Map<String, String>, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<AnswerSendResult?>

    @GET("/webservice/rest/server.php")
    fun getQuizFinished(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("attemptid") attemptid: String, @Query("page") page: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<QuizFinished?>

    @GET("/webservice/rest/server.php")
    fun getQuizFinished(@Query("wstoken") wsToken: String, @Query("wsfunction") wsFunction: String, @Query("attemptid") attemptid: String, @Query("moodlewsrestformat") moodlewsRestFormat: String): Call<QuizFinished?>
}

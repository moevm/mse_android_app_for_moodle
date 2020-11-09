package info.moevm.moodle.data.statistics

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
//the number of most popular success percent and number of students with it
@Immutable
data class SuccessCourses(
        val name: String,
        val number: Int,
        val percent: Float,
        val color: Color
)
//amount of the students with marks on certain courses
@Immutable
data class SuccessStudents(
        val course: String,
        val mark: String,
        val amount: Int,
        val color: Color
)

object UserData {
    val courses: List<SuccessCourses> = listOf(
            SuccessCourses(
                    "Введение в ПИ",
                    123,
                    0.25f,
                    Color(0xFF004940)
            ),
            SuccessCourses(
                    "HL Assembly",
                    9999,
                    0.75f,
                    Color(0xFF005D57)
            ),
            SuccessCourses(
                    "Информатика(Python)",
                    9012,
                    0.95f,
                    Color(0xFF04B97F)
            ),
            SuccessCourses(
                    "WEB-программирование",
                    3456,
                    0.12f,
                    Color(0xFF37EFBA)
            )
    )
    val students: List<SuccessStudents> = listOf(
            SuccessStudents(
                    "Введение в Программную Инженерию",
                    "Отлично",
                    55,
                    Color(0xFFFFDC78)
            ),
            SuccessStudents(
                    "Введение в Программную Инженерию",
                    "Хорошо",
                    66,
                    Color(0xFFFF6951)
            ),
            SuccessStudents(
                    "Введение в Программную Инженерию",
                    "Удовлетворительно",
                    77,
                    Color(0xFFFFD7D0)
            ),
            SuccessStudents(
                    "Введение в Программную Инженерию",
                    "Плохо",
                    88,
                    Color(0xFFFFAC12)
            ),
            SuccessStudents(
                    "Введение в Программную Инженерию",
                    "Не аттестован",
                    10,
                    Color(0xFFFFAC12)
            )
    )
}

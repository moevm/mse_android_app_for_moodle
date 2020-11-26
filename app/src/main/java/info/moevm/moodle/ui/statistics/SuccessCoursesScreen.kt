package info.moevm.moodle.ui.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import info.moevm.moodle.R
import info.moevm.moodle.data.statistics.SuccessCourses
import info.moevm.moodle.ui.components.StatementBody
import info.moevm.moodle.ui.components.SuccessCoursesRow

@Composable
fun SuccessCoursesBody(courses: List<SuccessCourses>) {
    StatementBody(
        items = courses,
        amounts = { course -> course.percent },
        colors = { course -> course.color },
        amountsTotal = courses.map { course -> course.percent }.sum(),
        circleLabel = stringResource(R.string.total),
        rows = { course ->
            SuccessCoursesRow(
                name = course.name,
                number = course.number,
                percent = course.percent,
                color = course.color
            )
        }
    )
}

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
            amounts = { account -> account.percent },
            colors = { account -> account.color },
            amountsTotal = courses.map { account -> account.percent }.sum(),
            circleLabel = stringResource(R.string.total),
            rows = { account ->
                SuccessCoursesRow(
                        name = account.name,
                        number = account.number,
                        percent = account.percent,
                        color = account.color
                )
            }
    )
}
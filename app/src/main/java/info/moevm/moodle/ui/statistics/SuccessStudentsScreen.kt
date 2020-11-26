package info.moevm.moodle.ui.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import info.moevm.moodle.R
import info.moevm.moodle.data.statistics.SuccessStudents
import info.moevm.moodle.ui.components.StatementBody
import info.moevm.moodle.ui.components.SuccessStudentsRow

/**
 * The Bills screen.
 */
@Composable
fun SuccessStudentsBody(students: List<SuccessStudents>) {
    StatementBody(
        items = students,
        amounts = { student -> student.amount * 1f },
        colors = { student -> student.color },
        amountsTotal = students.map { student -> student.amount * 1f }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { student ->
            SuccessStudentsRow(student.course, student.mark, student.amount, student.color)
        }
    )
}

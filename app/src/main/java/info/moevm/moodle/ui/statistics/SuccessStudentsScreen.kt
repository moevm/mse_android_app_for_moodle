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
fun SuccessStudentsBody(bills: List<SuccessStudents>) {
    StatementBody(
        items = bills,
        amounts = { bill -> bill.amount * 1f },
        colors = { bill -> bill.color },
        amountsTotal = bills.map { bill -> bill.amount * 1f }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { bill ->
            SuccessStudentsRow(bill.course, bill.mark, bill.amount, bill.color)
        }
    )
}

package info.moevm.moodle.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import info.moevm.moodle.data.statistics.UserData
import info.moevm.moodle.ui.statistics.SuccessCoursesBody
import info.moevm.moodle.ui.statistics.SuccessStudentsBody

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 */
enum class SettingsScreen(
        val icon: VectorAsset,
        val body: @Composable ((SettingsScreen) -> Unit) -> Unit
) {
    Overview(
            icon = Icons.Filled.PieChart,
            body = { onScreenChange -> OverviewBody(onScreenChange) }
    ),
    Courses(
            icon = Icons.Filled.AttachMoney,
            body = { SuccessCoursesBody(UserData.courses) }
    ),
    Students(
            icon = Icons.Filled.MoneyOff,
            body = { SuccessStudentsBody(UserData.students) }
    );

    @Composable
    fun content(onScreenChange: (SettingsScreen) -> Unit) {
        body(onScreenChange)
    }
}




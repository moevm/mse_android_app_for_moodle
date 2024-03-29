package info.moevm.moodle.ui.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import info.moevm.moodle.data.statistics.UserData

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 */
enum class SettingsScreenForStatistics(
    val icon: ImageVector,
    val body: @Composable ((SettingsScreenForStatistics) -> Unit) -> Unit
) {
    Overview(
        icon = Icons.Filled.Analytics,
        body = { onScreenChange -> OverviewBody(onScreenChange) }
    ),
    Courses(
        icon = Icons.Filled.Assessment,
        body = { SuccessCoursesBody(UserData.courses) }
    ),
    Students(
        icon = Icons.Filled.AssignmentInd,
        body = { SuccessStudentsBody(UserData.students) }
    );

    @Composable
    fun Content(onScreenChange: (SettingsScreenForStatistics) -> Unit) {
        body(onScreenChange)
    }
}

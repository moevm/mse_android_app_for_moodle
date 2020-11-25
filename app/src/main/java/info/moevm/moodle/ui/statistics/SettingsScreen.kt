package info.moevm.moodle.ui.statistics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import info.moevm.moodle.data.statistics.UserData

/**
 * Screen state for Rally. Navigation is kept simple until a proper mechanism is available. Back
 * navigation is not supported.
 */
enum class SettingsScreen(
    val icon: VectorAsset,
    val body: @Composable ((SettingsScreen) -> Unit) -> Unit
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
    fun content(onScreenChange: (SettingsScreen) -> Unit) {
        body(onScreenChange)
    }
}

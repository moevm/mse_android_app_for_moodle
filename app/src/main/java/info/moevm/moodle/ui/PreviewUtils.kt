package info.moevm.moodle.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    children: @Composable () -> Unit
) {
    MOEVMMoodleTheme(darkTheme = darkTheme) {
        Surface {
            children()
        }
    }
}
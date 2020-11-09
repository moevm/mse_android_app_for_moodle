package info.moevm.moodle.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import info.moevm.moodle.MOEVMMoodleApplication
import info.moevm.moodle.ui.components.RallyTopAppBar
import info.moevm.moodle.ui.theme.MOEVMMoodleTheme

class MainActivity : AppCompatActivity() {

    private val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as MOEVMMoodleApplication).container
        setContent { // call Composable
            MOEVMMoodleApp(appContainer, navigationViewModel)
        }
    }

    /**
     * Exit from app, if back button was pressed on main screen
     */
    override fun onBackPressed() {
        if (!navigationViewModel.onBack()) {
            super.onBackPressed()
        }
    }
}

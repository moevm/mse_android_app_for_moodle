package info.moevm.moodle.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import info.moevm.moodle.MOEVMMoodleApplication

class MainActivity: AppCompatActivity() {

    val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as MOEVMMoodleApplication).container
        setContent {    // call Composable
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
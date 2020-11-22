package info.moevm.moodle.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import info.moevm.moodle.MOEVMMoodleApplication

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MOEVMMoodleApp(
                (application as MOEVMMoodleApplication).container
            )
        }
    }
}

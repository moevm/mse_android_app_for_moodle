package info.moevm.moodle.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import info.moevm.moodle.MOEVMMoodleApplication
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate was called")

        setContent {
            MOEVMMoodleApp(
                (application as MOEVMMoodleApplication).container
            )
        }
    }
}

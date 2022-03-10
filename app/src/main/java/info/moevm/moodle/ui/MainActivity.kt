package info.moevm.moodle.ui

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import info.moevm.moodle.MOEVMMoodleApplication
import info.moevm.moodle.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate was called")
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                MOEVMMoodleApp(
                    (application as MOEVMMoodleApplication).container
                )
            }
        }
    }
}

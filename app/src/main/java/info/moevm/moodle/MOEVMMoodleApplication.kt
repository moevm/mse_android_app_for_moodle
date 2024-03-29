package info.moevm.moodle

/**
 * Just read this. Believe me: without it, you will feel bad.
 * https://developer.android.com/training/dependency-injection/manual
 */

import android.app.Application
import info.moevm.moodle.data.AppContainer
import info.moevm.moodle.data.AppContainerImpl
import timber.log.Timber
import timber.log.Timber.DebugTree

class MOEVMMoodleApplication : Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies
    // it's field injection
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // print logs in debug mode
            Timber.plant(DebugTree())
        }

        container = AppContainerImpl(this)
    }
}

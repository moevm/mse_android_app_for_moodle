/**
 * It's something similar as we use to do with Jetpack Navigation but without XML
 */

package info.moevm.moodle.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Destination : Parcelable {

    @Parcelize
    object Home : Destination()

    @Parcelize
    object Interests : Destination()

    @Parcelize
    object Login : Destination()

    @Parcelize
    object Article : Destination()
}

class Actions(navigator: Navigator<Destination>) {

    val login: () -> Unit = {
        navigator.navigate(Destination.Home)
    }

    val pressOnBack: () -> Unit = {
        navigator.back()
    }
}

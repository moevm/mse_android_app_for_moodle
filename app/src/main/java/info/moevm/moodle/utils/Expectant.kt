package info.moevm.moodle.utils

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class Expectant {
    companion object {
        fun waitSomeSecondUntilFalse(flag: MutableStateFlow<Boolean>, seconds: Long) {
            val time = System.currentTimeMillis()
            val waitingTime = TimeUnit.SECONDS.toMillis(seconds)
            while (!flag.value && System.currentTimeMillis() - time < waitingTime) {}
        }
    }
}

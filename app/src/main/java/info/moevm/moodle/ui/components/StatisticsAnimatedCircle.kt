package info.moevm.moodle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

//private const val DividerLengthInDegrees = 1.8f
//private val AngleOffset = FloatPropKey()
//private val Shift = FloatPropKey()

/**
 * A donut chart that animates when loaded.
 */
//@Composable
//fun AnimatedCircle(
//    proportions: List<Float>,
//    colors: List<Color>,
//    modifier: Modifier = Modifier
//) {
//    val stroke = with(LocalDensity.current) { Stroke(5.dp.toPx()) }
//
//    val transitionState = remember{
//        MutableTransitionState(AnimatedCircleProgress.START).apply {
//            targetState = AnimatedCircleProgress.END
//        }
//    }
//    val transition = updateTransition(transitionState, label = "")
//
//    val state = transition(
//        definition = CircularTransition,
//        initState = AnimatedCircleProgress.START,
//        toState = AnimatedCircleProgress.END
//    )
//    Canvas(modifier) {
//        val innerRadius = (size.minDimension - stroke.width) / 2
//        val halfSize = size / 2.0f
//        val topLeft = Offset(
//            halfSize.width - innerRadius,
//            halfSize.height - innerRadius
//        )
//        val size = Size(innerRadius * 2, innerRadius * 2)
//        var startAngle = state[Shift] - 90f
//        proportions.forEachIndexed { index, proportion ->
//            val sweep = proportion * state[AngleOffset]
//            drawArc(
//                color = colors[index],
//                startAngle = startAngle + DividerLengthInDegrees / 2,
//                sweepAngle = sweep - DividerLengthInDegrees,
//                topLeft = topLeft,
//                size = size,
//                useCenter = false,
//                style = stroke
//            )
//            startAngle += sweep
//        }
//    }
//}
//private enum class AnimatedCircleProgress { START, END }
//
//private val CircularTransition = transitionDefinition<AnimatedCircleProgress> {
//    state(AnimatedCircleProgress.START) {
//        this[AngleOffset] = 0f
//        this[Shift] = 0f
//    }
//    state(AnimatedCircleProgress.END) {
//        this[AngleOffset] = 360f
//        this[Shift] = 30f
//    }
//    transition(fromState = AnimatedCircleProgress.START, toState = AnimatedCircleProgress.END) {
//        AngleOffset using tween(
//            delayMillis = 500,
//            durationMillis = 900,
//            easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
//        )
//        Shift using tween(
//            delayMillis = 500,
//            durationMillis = 900,
//            easing = LinearOutSlowInEasing
//        )
//    }
//}

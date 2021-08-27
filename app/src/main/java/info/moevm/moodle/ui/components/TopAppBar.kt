package info.moevm.moodle.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import info.moevm.moodle.ui.statistics.SettingsScreenForStatistics
import java.util.*

@Composable
fun StatisticsTopAppBar(
    allScreens: List<SettingsScreenForStatistics>,
    onTabSelected: (SettingsScreenForStatistics) -> Unit,
    currentScreen: SettingsScreenForStatistics
) {
    Surface(Modifier.height(TabHeight).fillMaxWidth()) {
        Row {
            allScreens.forEach { screen ->
                SettingsTab(
                    text = screen.name.uppercase(Locale.ROOT),
                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}

@Composable
private fun SettingsTab(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color = MaterialTheme.colors.onSurface
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor = animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .height(TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected
//                indication = rememberRipple(bounded = false) ???
            )
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tabTintColor.value)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Text(text = text, color = tabTintColor.value)
        }
    }
}

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

package info.moevm.moodle.ui.components

import info.moevm.moodle.R
import androidx.compose.animation.animate
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import info.moevm.moodle.ui.statistics.SettingsScreenForStatistics
import java.util.*

@Composable
fun StatisticsTopAppBar(
    scaffoldState: ScaffoldState,
    allScreens: List<SettingsScreenForStatistics>,
    onTabSelected: (SettingsScreenForStatistics) -> Unit,
    currentScreen: SettingsScreenForStatistics
) {
    Surface(
        Modifier
            .preferredHeight(TabHeight)
            .fillMaxWidth()) {
        Row {
            IconButton(
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp),
                onClick = { scaffoldState.drawerState.open() }) {
                Icon(vectorResource(R.drawable.ic_logo_light))
            }
            allScreens.forEach { screen ->
                SettingsTab(
                    text = screen.name.toUpperCase(Locale.ROOT),
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
    val tabTintColor = animate(
        target = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .preferredHeight(TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected,
                indication = rememberRippleIndication(bounded = false)
            )
    ) {
        Icon(imageVector = icon, tint = tabTintColor)
        if (selected) {
            Spacer(Modifier.preferredWidth(12.dp))
            Text(text, color = tabTintColor)
        }
    }
}

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

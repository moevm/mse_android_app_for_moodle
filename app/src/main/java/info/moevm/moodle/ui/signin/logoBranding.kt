package info.moevm.moodle.ui.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R

fun Modifier.brandingPreferredHeight(
    showBranding: Boolean,
    heightDp: Dp
): Modifier {
    return if (!showBranding) {
        this.wrapContentHeight(unbounded = true)
            .preferredHeight(heightDp)
    } else {
        this
    }
}

@Composable
fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 76.dp))
        Providers(
            AmbientContentAlpha provides ContentAlpha.high,
            children = {
                Text(
                    text = stringResource(id = R.string.app_tagline),
                    style = MaterialTheme.typography.subtitle1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun Logo(
    lightTheme: Boolean = MaterialTheme.colors.isLight,
    modifier: Modifier = Modifier
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        asset = vectorResource(id = assetId),
        modifier = modifier
    )
}

package info.moevm.moodle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.layout.ContentScale

@Composable
fun CircularImage(modifier: Modifier, image: ImageAsset) {
    Image(
        asset = image,
        modifier = Modifier.clip(CircleShape)
            then modifier,
        contentScale = ContentScale.Crop
    )
}

package info.moevm.moodle.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import info.moevm.moodle.R
import info.moevm.moodle.model.*

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    cardContent: @Composable () -> Unit = {},
    card: ExpandableCardModel,
    onCardArrowClick: () -> Unit,
    expanded: Boolean,
    dividerColor: MutableState<Color>
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(
        transitionState = transitionState,
        label = "ExpandableCard"
    )
    val cardBgColor by transition.animateColor(
        {
            tween(durationMillis = EXPAND_ANIMATION_DURATION)
        },
        label = "CardBackgroundColor"
    ) {
        Color.White
    }
    val cardPaddingHorizontal by transition.animateDp(
        {
            tween(durationMillis = EXPAND_ANIMATION_DURATION)
        },
        label = "CardPaddingHorizontal"
    ) {
        16.dp
    }
//    val cardRoundedCorners by transition.animateDp({
//        tween(
//            durationMillis = EXPAND_ANIMATION_DURATION,
//            easing = FastOutSlowInEasing
//        )
//    }, label = "CardRoundedCorners") {
//        if(expanded) 4.dp else 16.dp
//    }
    val arrowRotationDegree by transition.animateFloat(
        {
            tween(durationMillis = EXPAND_ANIMATION_DURATION)
        },
        label = "ArrowRotationDegree"
    ) {
        if (expanded) 180f else 0f
    }
    Card(
        backgroundColor = cardBgColor,
        contentColor = Color(
            ContextCompat.getColor(
                LocalContext.current,
                R.color.cardview_dark_background
            )
        ),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = 8.dp
            )
    ) {
        Column(Modifier.fillMaxWidth()) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val boxScope = this
                Row {
                    CardTitle(
                        modifier = Modifier
                            .padding(4.dp)
                            .width(boxScope.maxWidth - 65.dp)
                            .align(Alignment.CenterVertically),
                        title = card.title,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.15.sp,
                            fontFamily = FontFamily.Default
                        )
                    )
                    CardArrow(
                        modifier = Modifier.padding(top = 4.dp),
                        degrees = arrowRotationDegree,
                        onClick = onCardArrowClick
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                color = dividerColor.value,
                thickness = 1.dp
            )
            ExpandableBottomContent(cardItem = cardContent, visible = expanded)
        }
    }
}

@Composable
fun CardTitle(
    modifier: Modifier,
    textStyle: TextStyle,
    title: String
) {
    Text(
        text = title,
        modifier = modifier,
        textAlign = TextAlign.Left,
        style = textStyle
    )
}

@Composable
fun CardArrow(
    modifier: Modifier,
    degrees: Float,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees)
            )
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableBottomContent(
    cardItem: @Composable () -> Unit,
    visible: Boolean = true
) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    val enterExpand = remember {
        expandVertically(animationSpec = tween(EXPAND_ANIMATION_DURATION))
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION
            )
        )
    }
    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(COLLAPSE_ANIMATION_DURATION))
    }
    AnimatedVisibility(
        visible = visible,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            cardItem()
        }
    }
}

@Preview(name = "Less")
@Composable
fun ExpandableCardPreviewLess() {
    val cardsViewModel = CardsViewModel(listOf())
    val expandedCardIds = cardsViewModel.expandedCardIdsList.collectAsState()
    ExpandableCard(
        card = ExpandableCardModel(0, "Title"),
        onCardArrowClick = { cardsViewModel.onCardArrowClicked(0) },
        expanded = expandedCardIds.value.contains(0),
        dividerColor = remember { mutableStateOf(Color.Magenta) }
    )
}

@Preview(name = "More")
@Composable
fun ExpandableCardPreviewMore() {
    val cardsViewModel = CardsViewModel(listOf())
    ExpandableCard(
        card = ExpandableCardModel(0, "Title"),
        onCardArrowClick = { cardsViewModel.onCardArrowClicked(0) },
        expanded = true,
        dividerColor = remember { mutableStateOf(Color.LightGray) }
    )
}

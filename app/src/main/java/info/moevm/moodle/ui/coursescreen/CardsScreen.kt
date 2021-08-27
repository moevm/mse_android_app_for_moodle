package info.moevm.moodle.ui.coursescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import info.moevm.moodle.R
import info.moevm.moodle.model.CardsViewModel
import info.moevm.moodle.model.MarkupType
import info.moevm.moodle.ui.components.ExpandableCard
import info.moevm.moodle.ui.Actions

enum class TaskType { TOPIC, TEST }
enum class TaskStatus { NONE, WORKING, DONE }


@Composable
fun CardsScreen(
    CourseName: String,
    CourseData: Map<String, List<String>>,
    CardsViewModel: CardsViewModel,
    onBack: () -> Unit
) {
    val cards = CardsViewModel.cards.collectAsState()
    val expandedCardIds = CardsViewModel.expandedCardIdsList.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topAppBarInterests"),
                title = { Text(CourseName) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("onBack"),
                        onClick = {
                            onBack()
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_left_32px),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        backgroundColor = Color(
            ContextCompat.getColor(
                LocalContext.current,
                android.R.color.white
            )
        )
    ) {
        LazyColumn(Modifier.padding(bottom = 4.dp)) {
            itemsIndexed(cards.value) { _, card ->
                val primaryColor = MaterialTheme.colors.primary
                val dividerColor = remember { mutableStateOf(primaryColor) }
                ExpandableCard(
                    cardContent = {
                        CardScreenItem(
                            taskType = 1,
                            taskTitle = "",
                            taskStatus = 1
                        )
                    },
                    card = card,
                    onCardArrowClick = {
                        dividerColor.value = if(dividerColor.value == primaryColor) Color.LightGray else primaryColor
                        CardsViewModel.onCardArrowClicked(card.id)
                    },
                    expanded = expandedCardIds.value.contains(card.id),
                    dividerColor = dividerColor
                )

            }
        }
    }
}

@Composable
fun CardScreenItem(taskType: Int, taskTitle: String, taskStatus: Int) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        val boxScope = this
        Column {
            Row(Modifier.clickable {  }) {
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.test_logo),
                    contentDescription = "taskType",
                    modifier = Modifier
                        .width(42.dp + 20.dp)
                        .height(42.dp)
                        .padding(start = 15.dp, end = 5.dp)
                )
                Text(
                    text = "Test Title",
                    modifier = Modifier
                        .width(boxScope.maxWidth - 42.dp * 2 - 20.dp - 17.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.15.sp,
                        fontFamily = FontFamily.Default
                    )
                )
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.test_working),
                    contentDescription = "taskStatus",
                    modifier = Modifier
                        .width(42.dp + 20.dp)
                        .height(42.dp)
                        .padding(start = 12.dp, end = 5.dp)

                )
            }
//            Spacer(modifier = )
            Divider(
                modifier = Modifier.padding(start = 15.dp, end = 6.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }
    }
}

@Preview
@Composable
fun CardsScreenPreview() {
    CardsScreen(CardsViewModel = CardsViewModel(listOf()), onBack = Actions(NavHostController(LocalContext.current)).upPress, CourseName = "Title", CourseData = mapOf())
}

@Preview(backgroundColor = R.color.cardview_light_background.toLong())
@Composable
fun CardsScreenItemPreview() {
    CardScreenItem(1,"test",2)
}

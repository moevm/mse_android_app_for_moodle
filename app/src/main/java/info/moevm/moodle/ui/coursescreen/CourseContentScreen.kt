package info.moevm.moodle.ui.coursescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import info.moevm.moodle.R
import info.moevm.moodle.data.courses.exampleCourseContent
import info.moevm.moodle.model.CardsViewModel
import info.moevm.moodle.ui.components.ExpandableCard
import info.moevm.moodle.ui.Actions
import timber.log.Timber
import kotlin.IllegalArgumentException


@Composable
fun CourseContentScreen(
    CourseName: String,
    CourseMapData: CourseMapData,
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null)
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
                        CardItems(
                            tasksType = CourseMapData[CourseName]?.find { it.lessonTitle == card.title }?.lessonContent?.map{ it.taskType }?.toList().orEmpty(),
                            tasksTitles = CourseMapData[CourseName]?.find { it.lessonTitle == card.title }?.lessonContent?.map{ it.taskTitle }?.toList().orEmpty(),
                            tasksStatus = CourseMapData[CourseName]?.find { it.lessonTitle == card.title }?.lessonContent?.map{ it.taskStatus }?.toList().orEmpty()
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
fun CardItems(tasksType: List<TaskType>, tasksTitles: List<String>, tasksStatus: List<TaskStatus>) {
    try {
        if(tasksType.size != tasksTitles.size && tasksTitles.size != tasksStatus.size)
            throw IllegalArgumentException()
    } catch (e : IllegalArgumentException) {
        Timber.e("Lists have different lengths")
        return Column {
        }
    }
    Column {
        for (i in tasksType.indices) {
            CardItem(taskType = tasksType[i], taskTitle = tasksTitles[i], taskStatus = tasksStatus[i])
        }
    }
}

@Composable
fun CardItem(taskType: TaskType, taskTitle: String, taskStatus: TaskStatus) {
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        val boxScope = this
        Column(Modifier.clickable {  }) {
            Row(Modifier.padding(top = 8.dp, bottom = 15.dp)) {
                Image(
                    bitmap = ImageBitmap.imageResource(id = getTaskTypeIconId(taskType)),
                    contentDescription = "taskType",
                    modifier = Modifier
                        .width(24.dp + 20.dp)
                        .height(24.dp)
                        .padding(start = 15.dp, end = 5.dp)
                )
                Text(
                    text = taskTitle,
                    modifier = Modifier
                        .width(boxScope.maxWidth - 24.dp * 2 - 20.dp - 17.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.15.sp,
                        fontFamily = FontFamily.Default
                    )
                )
                Image(
                    bitmap = ImageBitmap.imageResource(id = getTaskStatusIconId(taskStatus)),
                    contentDescription = "taskStatus",
                    modifier = Modifier
                        .width(24.dp + 17.dp)
                        .height(24.dp)
                        .padding(start = 12.dp, end = 5.dp)
                )
            }
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
fun CourseContentScreenPreview() {
    CourseContentScreen(CardsViewModel = CardsViewModel(exampleCourseContent().values.first().map { it.lessonTitle }), onBack = Actions(NavHostController(LocalContext.current)).upPress, CourseName = "Title", CourseMapData = exampleCourseContent())
}

@Preview(backgroundColor = R.color.cardview_light_background.toLong())
@Composable
fun CardsScreenItemPreview() {
    CardItem(TaskType.TOPIC,"test",TaskStatus.WORKING)
}

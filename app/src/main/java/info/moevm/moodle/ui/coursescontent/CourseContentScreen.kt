package info.moevm.moodle.ui.coursescontent

import android.text.Html
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import info.moevm.moodle.R
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.model.CardsViewModel
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.components.ExpandableCard
import info.moevm.moodle.ui.components.LoadErrorActivity
import info.moevm.moodle.ui.signin.showMessage
import info.moevm.moodle.utils.Expectant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.IllegalArgumentException

@Composable
fun CourseContentScreen(
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit,
    onBackPressed: () -> Unit
) {
//    TODO: исправить на вывод ошибки
    val waitTime = 1500
    if (courseManager.getLessonsTitles() == null) { // если вдруг не успело загрузиться
        val startTime = System.currentTimeMillis()
        while (courseManager.getLessonsTitles() == null && System.currentTimeMillis() - startTime < waitTime) {}
    }
    val titles = courseManager.getLessonsTitles() // если всё же не загрузилось,
    val availableTypes = TaskType.values().map { it.value }

    while (System.currentTimeMillis() - waitTime < 250) { // FIXME: Даём время на удаление прошлого содержимого, исправить
        Timber.i("content TimeOut 250ms")
    }

    val cardsViewModel = CardsViewModel(titles)
    val cards = cardsViewModel.cards.collectAsState()
    val expandedCardIds = cardsViewModel.expandedCardIdsList.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topAppBarInterests"),
                title = {
                    Text(
                        fontSize = 15.sp,
                        text = courseManager.getCourseName()
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
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
        if (titles == null || titles.isEmpty()) {
            LoadErrorActivity()
            return@Scaffold
        }
        LazyColumn(Modifier.padding(bottom = 4.dp)) {
            itemsIndexed(cards.value) { index, card ->
                val primaryColor = MaterialTheme.colors.primary
                val dividerColor = remember { mutableStateOf(primaryColor) }
                ExpandableCard(
                    cardContent = {
                        val lessonContent =
                            courseManager.getLessonsContents(index)
                        if (lessonContent != null) {
                            CardItems( // TODO: исправить на нормально
                                tasksType = lessonContent.map { if((it.modname ?: "") in availableTypes) TaskType.valueOf(it.modname?.uppercase() ?: "") else TaskType.NONE}.toList(),
                                tasksTitles = lessonContent.map { Html.fromHtml(it.name).toString() ?: "<Ошибка загрузки данных>" }.toList(),
                                tasksStatus = lessonContent.map {
                                    when (it.completiondata?.state) {
                                        TaskStatus.DONE.value -> TaskStatus.DONE
                                        TaskStatus.FAILED.value -> TaskStatus.FAILED
                                        TaskStatus.WORKING.value -> TaskStatus.WORKING
                                        TaskStatus.RELOAD.value -> TaskStatus.RELOAD
                                        else -> TaskStatus.NONE
                                    }
                                },
                                categoryLessonIndex = index,
                                courseManager = courseManager,
                                navigateTo = navigateTo
                            )
                        }
                    },
                    card = card,
                    onExpandableClick = {
                        dividerColor.value =
                            if (dividerColor.value == primaryColor) Color.LightGray else primaryColor
                        cardsViewModel.onCardClicked(card.id)
                    },
                    expanded = expandedCardIds.value.contains(card.id),
                    dividerColor = dividerColor
                )
            }
        }
    }
}

@Composable
fun CardItems(
    tasksType: List<TaskType>,
    tasksTitles: List<String>,
    tasksStatus: List<TaskStatus>,
    courseManager: CourseManager,
    categoryLessonIndex: Int,
    navigateTo: (Screen) -> Unit
) {
    try {
        if (tasksType.size != tasksTitles.size && tasksTitles.size != tasksStatus.size)
            throw IllegalArgumentException()
    } catch (e: IllegalArgumentException) {
        Timber.e("Lists have different lengths")
        return Column {
        }
    }
    Column {
        for (i in tasksType.indices) {
            if (courseManager.getLessonsItemInstanceId(categoryLessonIndex, i) != null) {
                CardItem(
                    taskType = tasksType[i],
                    taskTitle = tasksTitles[i],
                    taskStatus = tasksStatus[i],
                    courseManager = courseManager,
                    categoryLessonIndex = categoryLessonIndex,
                    lessonIndex = i,
                    lessonId = courseManager.getLessonsItemInstanceId(categoryLessonIndex, i)!!,
                    navigateTo = navigateTo
                )
            }
        }
    }
}

@Composable
fun CardItem(
    taskType: TaskType,
    taskTitle: String,
    taskStatus: TaskStatus,
    courseManager: CourseManager,
    categoryLessonIndex: Int,
    lessonIndex: Int,
    lessonId: Int,
    navigateTo: (Screen) -> Unit
) {
    val context = LocalContext.current
    BoxWithConstraints(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        val boxScope = this
        Column(
            Modifier.clickable {
                // загрузка данных
                when (taskType) {
                    TaskType.LESSON -> {
                        courseManager.receiveLessonPages(lessonId)
                        courseManager.setCategoryLessonIndex(categoryLessonIndex)
                        courseManager.setLessonIndex(lessonIndex)
                        courseManager.setTaskIndex(0)
                        courseManager.setGlobalItem()
                        navigateTo(Screen.Article)
                    }
                    TaskType.QUIZ -> {
                        courseManager.receiveQuizAttempts(lessonId.toString())
                        courseManager.setCategoryLessonIndex(categoryLessonIndex)
                        courseManager.setLessonIndex(lessonIndex)
                        courseManager.setTaskIndex(0)
                        courseManager.setLocalQuizId(lessonId.toString())
                        navigateTo(Screen.TestAttempts)
                    }
                    else -> showMessage(context, context.getString(R.string.not_support_element_course))
                }
            }
        ) {
            Row(Modifier.padding(top = 8.dp, bottom = 15.dp)) {
                Image(
                    bitmap = ImageBitmap.imageResource(
                        id = getTaskTypeIconId(
                            taskType
                        )
                    ),
                    contentDescription = "taskType",
                    modifier = Modifier
                        .width(24.dp + 20.dp)
                        .height(24.dp)
                        .padding(start = 15.dp, end = 5.dp)
                )
                Text(
                    text = taskTitle,
                    modifier = Modifier
                        .width(boxScope.maxWidth - 24.dp * 2 - 20.dp - 25.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.15.sp,
                        fontFamily = FontFamily.Default
                    )
                )
                IconButton(
                    modifier = Modifier
                        .width(24.dp + 17.dp)
                        .height(24.dp)
                        .padding(start = 12.dp, end = 5.dp),
                    enabled = taskStatus == TaskStatus.RELOAD,
                    onClick = { /*TODO*/ }
                ) {
                    Image(
                        modifier = Modifier
                            .width(24.dp + 17.dp)
                            .height(24.dp),
                        bitmap = ImageBitmap.imageResource(
                            id = getTaskStatusIconId(
                                taskStatus
                            )
                        ),
                        contentDescription = "taskStatus",
                    )
                }
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
//    CourseContentScreen(CardsViewModel = CardsViewModel(exampleCourseContent().values.first().map { it.lessonTitle }), onBack = Actions(NavHostController(LocalContext.current)).upPress, CourseName = "Title", CourseMapData = exampleCourseContent())
}

@Preview(backgroundColor = R.color.cardview_light_background.toLong())
@Composable
fun CardsScreenItemPreview() {
//    CardItem(TaskType.TOPIC,"test",TaskStatus.WORKING)
}

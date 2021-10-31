package info.moevm.moodle.ui.coursecontent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescreen.*

@Composable
fun TestPreviewScreen(
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit
) {
    if (courseManager.requiredMoveLessonIndexForward) {
        courseManager.requiredMoveLessonIndexForward = false
        courseManager.moveLessonIndex(1)
    } else if (courseManager.requiredMoveLessonIndexBack) {
        courseManager.requiredMoveLessonIndexBack = false
        courseManager.moveLessonIndex(-1)
    }

    val lessonContent = courseManager.getTestLessonContent()
    val mapAttempts =
        remember { mutableStateMapOf<String, Pair<AttemptData, List<TaskContentItem?>>>() }
    mapAttempts.putAll(lessonContent?.taskContent.orEmpty())
    val taskContentItemSize =
        courseManager.getTestLessonContent()?.taskContent?.get(
            courseManager.getAttemptKey().value
        )?.second?.size ?: 1
    Scaffold(
        topBar = { TestPreviewScreenTopBar { navigateTo(Screen.CourseContent) } },
        bottomBar = {
            BottomNavigatorWithAttempt(
                mapAttempts = mapAttempts,
                courseManager = courseManager,
                taskContentItemSize = taskContentItemSize,
                navigateTo = navigateTo
            )
        }
    ) {
        if (lessonContent == null) {
            return@Scaffold
        }
        if (lessonContent.taskType == TaskType.NONE) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(20.dp),
                    text = "Ошибка загрузки данных"
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .size(60.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null
                    )
                }
            }
        }
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = lessonContent.taskTitle,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
//            Text(
//                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
//                text = lessonContent.taskMark,
//                style = TextStyle(
//                    fontSize = 12.sp,
//                    color = Color(0f, 0f, 0f, 0.4f),
//                    textAlign = TextAlign.Center
//                )
//            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp),
                    text = "Результаты ваших предыдущих попыток:",
                    style = MaterialTheme.typography.subtitle1
                )
                BoxWithConstraints {
                    val boxScope = this
                    Column {
                        BoxWithConstraints(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(modifier = Modifier.padding(start = 16.dp)) {
                                Text(text = "Попытка")
                                Text(
                                    modifier = Modifier.padding(start = 16.dp),
                                    text = "Состояние"
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .padding(end = 30.dp)
                                    .align(Alignment.CenterEnd),
                                text = "Просмотр"
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .padding(start = 14.dp, top = 20.dp)
                                .width(boxScope.maxWidth - 28.dp)
                        )
                    }
                }
                for (item in mapAttempts.values) {
                    AttemptsCard(
                        chosenAttempt = courseManager.getAttemptKey(),
                        id = item.first.id,
                        taskStatus = item.first.taskStatus,
                        date = item.first.date,
                        navigateTo = navigateTo
                    )
                }
            }
        }
    }
}

@Composable
fun AttemptsCard(
    chosenAttempt: MutableState<String>,
    id: Int,
    taskStatus: TaskStatus,
    date: String,
    navigateTo: (Screen) -> Unit
) {
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        val boxScope = this
        Column {
            BoxWithConstraints {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 40.dp),
                        text = id.toString()
                    )
                    Column(
                        Modifier
                            .padding(start = 50.dp)
                            .width(120.dp)
//                    .width(boxScope.maxWidth - 200.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = when (taskStatus) {
                                TaskStatus.DONE -> "Завершённые"
                                TaskStatus.WORKING -> "В Процессе"
                                else -> "error"
                            },
                            style = MaterialTheme.typography.body1
                        )
                        if (taskStatus == TaskStatus.DONE) {
                            Text(
                                modifier = Modifier.padding(top = 5.dp),
                                text = date,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                }
                TextButton(
                    modifier = Modifier
                        .padding(end = 25.dp)
                        .align(Alignment.CenterEnd),
                    onClick = {
                        chosenAttempt.value = id.toString()
                        navigateTo(Screen.Test)
                    }
                ) {
                    Text(
                        text = "Просмотр",
                        textDecoration = TextDecoration.Underline,
                        color = Color(0x62, 0x00, 0xFF, 80)
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .padding(start = 14.dp, top = 8.dp)
                    .width(boxScope.maxWidth - 28.dp)
            )
        }
    }
}

@Composable
fun TestPreviewScreenTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Элемент курса") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun BottomNavigatorWithAttempt(
    mapAttempts: SnapshotStateMap<String, Pair<AttemptData, List<TaskContentItem?>>>,
    courseManager: CourseManager,
    taskContentItemSize: Int,
    navigateTo: (Screen) -> Unit
) {
    val lessonContent = courseManager.getTestLessonContent()

    val (iconBack, textBack) = when (courseManager.getLessonContentItemIndex().value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when (courseManager.getLessonContentItemIndex().value) {
        courseManager.getLessonContentSize() - 1 -> Pair(
            Icons.Filled.Task,
            "Завершить"
        )
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
    val (iconAttempt, textAttempt) = Pair(
        Icons.Filled.Article,
        "Новая попытка"
    ) // Продолжить попытку ???

    val selectedItem by remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem( // Назад
            selected = selectedItem == 0,
            onClick = {
                if (courseManager.getTaskContentItemIndexState().value == 0) {
                    courseManager.requiredMoveLessonIndexBack = true
                    when (courseManager.getPrevLessonType()) {
                        TaskType.QUIZ -> navigateTo(Screen.PreviewTest)
                        TaskType.LESSON -> navigateTo(Screen.Article)
                        TaskType.NONE -> {}
                    }
                } else
                    courseManager.moveTaskIndex(-1)
            },
            icon = { Icon(imageVector = iconBack, contentDescription = null) },
            label = { Text(textBack) }
        )
        BottomNavigationItem( // Новая попытка
            selected = selectedItem == 1,
            onClick = {
                if (mapAttempts.values.last().first.taskStatus == TaskStatus.DONE) {
                    mapAttempts[mapAttempts.size.toString()] =
                        testData(mapAttempts.size)
                    lessonContent?.taskContent?.put(
                        lessonContent.taskContent.size.toString(),
                        testData(lessonContent.taskContent.size)
                    )
                }
            }, // FIXME исправить на нормально
            icon = {
                Icon(
                    imageVector = iconAttempt,
                    contentDescription = null
                )
            },
            label = { Text(textAttempt) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 2,
            onClick = {
                if (courseManager.getTaskContentItemIndexState().value == taskContentItemSize - 1) {
                    courseManager.requiredMoveLessonIndexForward = true
                    when (courseManager.getNextLessonType()) {
                        TaskType.QUIZ -> navigateTo(Screen.PreviewTest)
                        TaskType.LESSON -> navigateTo(Screen.Article)
                        TaskType.NONE -> {}
                    }
                } else
                    courseManager.moveTaskIndex(1)
            }, // FIXME исправить на нормально
            icon = {
                Icon(
                    imageVector = iconForward,
                    contentDescription = null
                )
            },
            label = { Text(textForward) }
        )
    }
}

fun testData(id: Int): Pair<AttemptData, List<TaskContentItem?>> {
    return Pair(
        AttemptData(
            id,
            "Отправлено 19.07.2021, 10:45",
            TaskStatus.DONE
        ),
        listOf()
    )
}

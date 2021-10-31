package info.moevm.moodle.ui.coursecontent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.data.courses.exampleCourseContent
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescreen.ArticleTaskContentItem
import info.moevm.moodle.ui.coursescreen.TaskType

@Composable
fun ArticleScreen(
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

    val lessonContent = courseManager.getArticleLessonContent()

    val taskContent =
        lessonContent?.taskContent?.get(courseManager.getTaskContentItemIndexState().value) as ArticleTaskContentItem?
    val scrollState = rememberScrollState()
    // FIXME моргание старого экрана при возвращении через "верхний" назад
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            TaskBottomNavigator(
                courseManager = courseManager,
                taskContentItemSize = lessonContent?.taskContent?.size ?: 1,
                navigateTo = navigateTo
            )
        }
    ) {
        if (lessonContent == null || taskContent == null) { // FIXME исправить появление Ошибки при перезоде между статьёй и тестом
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
                    onClick = { /*TODO*/ } // Повторная загрузка
                ) {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .fillMaxHeight()
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = taskContent.taskTitle,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = taskContent.taskMark,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0f, 0f, 0f, 0.4f),
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            taskContent.taskContent()
        }
    }
}

@Composable
fun TaskScreenTopBar(
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
fun TaskBottomNavigator(
    courseManager: CourseManager,
    taskContentItemSize: Int,
    navigateTo: (Screen) -> Unit
) {
    val (iconBack, textBack) = when (courseManager.getTaskContentItemIndexState().value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when (courseManager.getTaskContentItemIndexState().value) {
        taskContentItemSize - 1 -> Pair(Icons.Filled.Task, "Завершить")
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
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
            }, // FIXME исправить на нормально
            icon = { Icon(imageVector = iconBack, contentDescription = null) },
            label = { Text(textBack) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 1,
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

@Preview
@Composable
fun ArticleScreenPreview() {
    val courseContentItemIndex = remember { mutableStateOf(0) }
    val lessonContentItemIndex = remember { mutableStateOf(0) }
    val taskContentItemIndex = remember { mutableStateOf(0) }
    val content = exampleCourseContent()
//    ArticleScreen(
//        courseData = content.values.first(),
//        courseContentItemIndex = courseContentItemIndex,
//        lessonContentItemIndex = lessonContentItemIndex,
//        taskContentItemIndex = taskContentItemIndex,
//        navigateTo = { }
//    )
}

@Preview
@Composable
fun ArticleScreenBottomNavigatorPreview() {
//    ArticleScreenBottomNavigator(
//        listOf(),
//        remember { mutableStateOf(0) },
//        remember { mutableStateOf(0) },
//        remember { mutableStateOf(0) },
//        5
//    )
}

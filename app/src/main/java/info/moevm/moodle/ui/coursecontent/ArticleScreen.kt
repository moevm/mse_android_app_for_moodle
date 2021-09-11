package info.moevm.moodle.ui.coursecontent

import android.view.Gravity
import android.widget.Toast
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
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.moevm.moodle.data.courses.exampleCourseContent
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescreen.ArticleContentItems
import info.moevm.moodle.ui.coursescreen.ArticleTaskContentItem
import info.moevm.moodle.ui.coursescreen.CourseContentItem
import info.moevm.moodle.ui.coursescreen.CourseMapData

@Composable
fun ArticleScreen(
    courseData: List<CourseContentItem?>,
    courseContentItemIndex: MutableState<Int>,
    lessonContentItemIndex: MutableState<Int>,
    taskContentItemIndex: MutableState<Int>,
    navigateTo: (Screen) -> Unit
) {
    val lessonContent = courseData[courseContentItemIndex.value]?.lessonContent?.
        get(lessonContentItemIndex.value) as ArticleContentItems?
    val taskContent = lessonContent?.taskContent?.get(taskContentItemIndex.value) as ArticleTaskContentItem?
    val scrollState = rememberScrollState()
    // FIXME моргание старого экрана при возвращении через "верхний" назад

    Scaffold(
        topBar = {
            ArticleScreenTopBar(
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            ArticleScreenBottomNavigator(
                courseData = courseData,
                courseContentItemIndex = courseContentItemIndex,
                lessonContentItemIndex = lessonContentItemIndex,
                taskContentItemIndex = taskContentItemIndex,
                taskContentItemSize = lessonContent?.taskContent?.size ?: 1
            )
        }
    ) {
        if (lessonContent == null || taskContent == null) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Text(modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(20.dp) ,text = "Ошибка загрузки данных")
                IconButton(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp).size(60.dp), onClick = { /*TODO*/ }) {
                    Icon(modifier = Modifier.size(42.dp), imageVector = Icons.Filled.Refresh, contentDescription = null)
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
fun ArticleScreenTopBar(
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
fun ArticleScreenBottomNavigator(
    courseData: List<CourseContentItem?>,
    courseContentItemIndex: MutableState<Int>,
    lessonContentItemIndex: MutableState<Int>,
    taskContentItemIndex: MutableState<Int>,
    taskContentItemSize: Int
) {
    val (iconBack, textBack) = when (taskContentItemIndex.value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when (taskContentItemIndex.value) {
        taskContentItemSize - 1 -> Pair(Icons.Filled.Task, "Завершить")
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
    val selectedItem by remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem( // Назад
            selected = selectedItem == 0,
            onClick = {
                if (taskContentItemIndex.value - 1 >= 0) {
                    taskContentItemIndex.value--
                }
            }, // FIXME исправить на нормально
            icon = { Icon(imageVector = iconBack, contentDescription = null) },
            label = { Text(textBack) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 1,
            onClick = {
                if (taskContentItemIndex.value + 1 < taskContentItemSize) {
                    taskContentItemIndex.value++
                }
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
    ArticleScreen(
        courseData = content.values.first(),
        courseContentItemIndex = courseContentItemIndex,
        lessonContentItemIndex = lessonContentItemIndex,
        taskContentItemIndex = taskContentItemIndex,
        navigateTo = { }
    )
}

@Preview
@Composable
fun ArticleScreenBottomNavigatorPreview() {
    ArticleScreenBottomNavigator(
        listOf(),
        remember { mutableStateOf(0) },
        remember { mutableStateOf(0) },
        remember { mutableStateOf(0) },
        5
    )
}

package info.moevm.moodle.ui.coursecontent

import android.widget.Toast
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.moevm.moodle.data.courses.exampleCourseContent
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescreen.CourseContentItem
import info.moevm.moodle.ui.coursescreen.CourseMapData

@Composable
fun ArticleScreen(
    courseName: String,
    courseData: CourseMapData,
    courseContentItemIndex: MutableState<Int>,
    lessonContentItemIndex: MutableState<Int>,
    taskContentItemIndex: MutableState<Int>,
    navigateTo: (Screen) -> Unit
) {
    val taskContent = courseData[courseName]?.
    get(courseContentItemIndex.value)?.lessonContent?.
    get(lessonContentItemIndex.value)?.taskContent?.
    get(taskContentItemIndex.value)
    val scrollState = rememberScrollState()
    //FIXME моргание старого экрана при возвращении через "верхний" назад
    Scaffold(
        topBar = {
            ArticleScreenTopBar(
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            ArticleScreenBottomNavigator(
                courseData = courseData[courseName].orEmpty(),
                courseContentItemIndex = courseContentItemIndex,
                lessonContentItemIndex = lessonContentItemIndex,
                taskContentItemIndex = taskContentItemIndex,
                taskContentItemSize = courseData[courseName]!![courseContentItemIndex.value].lessonContent[lessonContentItemIndex.value].taskContent.size
            )
        }
    ) {
        if(taskContent == null) {
            Toast.makeText(LocalContext.current, "Произошла ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
            navigateTo(Screen.CourseContent)
            return@Scaffold //чтобы не использовать '?'
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = taskContent.taskTitle,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = taskContent.taskMark,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0f,0f,0f, 0.4f),
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
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
            IconButton(onClick = onBack ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun ArticleScreenBottomNavigator(
    courseData: List<CourseContentItem>,
    courseContentItemIndex: MutableState<Int>,
    lessonContentItemIndex: MutableState<Int>,
    taskContentItemIndex: MutableState<Int>,
    taskContentItemSize: Int
) {
    val (iconBack, textBack) = when(taskContentItemIndex.value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when(taskContentItemIndex.value) {
        taskContentItemSize - 1 -> Pair(Icons.Filled.Task, "Завершить")
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
    val selectedItem by remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem( // Назад
            selected = selectedItem == 0,
            onClick = { if(taskContentItemIndex.value - 1 >= 0) { taskContentItemIndex.value-- } },//FIXME исправить на нормально
            icon = { Icon(imageVector = iconBack, contentDescription = null)},
            label = { Text(textBack) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 0,
            onClick = { if(taskContentItemIndex.value + 1 < taskContentItemSize) { taskContentItemIndex.value++ } },//FIXME исправить на нормально
            icon = { Icon(imageVector = iconForward, contentDescription = null)},
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
        courseName = content.keys.first(),
        courseData = content,
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

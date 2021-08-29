package info.moevm.moodle.ui.coursecontent

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import info.moevm.moodle.ui.Screen
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
    taskContentItemIndex.value = courseData[courseName]!![courseContentItemIndex.value].lessonContent[lessonContentItemIndex.value].taskContent.size - 2
    Scaffold(
        topBar = {
            ArticleScreenTopBar(
                courseName = courseName,
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            ArticleScreenBottomNavigator(
                courseData = courseData,
                courseContentItemIndex = courseContentItemIndex,
                lessonContentItemIndex = lessonContentItemIndex,
                taskContentItemIndex = taskContentItemIndex,
                taskContentItemSize = courseData[courseName]!![courseContentItemIndex.value].lessonContent[lessonContentItemIndex.value].taskContent.size,
                navigateTo = navigateTo
            )
        }
    ) {

    }
}


@Composable
fun ArticleScreenBottomNavigator(
    courseData: CourseMapData,
    courseContentItemIndex: MutableState<Int>,
    lessonContentItemIndex: MutableState<Int>,
    taskContentItemIndex: MutableState<Int>,
    taskContentItemSize: Int,
    navigateTo: (Screen) -> Unit
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
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = iconBack, contentDescription = null)},
            label = { Text(textBack) }
        )
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = iconForward, contentDescription = null)},
            label = { Text(textForward) }
        )
    }
}

@Composable
fun ArticleScreenTopBar(
    courseName: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(courseName) },
        navigationIcon = {
            IconButton(onClick = onBack ) {
                Icon(imageVector = Icons.Filled.ArrowLeft, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
fun ArticleScreenBottomNavigatorPreview() {
    ArticleScreenBottomNavigator(
        mapOf(),
        remember { mutableStateOf(0) },
        remember { mutableStateOf(0) },
        remember { mutableStateOf(0) },
        5,
        {}
    )
}

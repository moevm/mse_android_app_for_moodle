package info.moevm.moodle.ui.lessoncontent

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.components.LoadErrorActivity
import info.moevm.moodle.ui.coursescontent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TestAttemptsScreen(
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit,
    onBackPressed: () -> Unit
) {
    // TODO проверить работу добавления новой попытки
    val attemptContent = courseManager.getQuizAttemptContent()
    val badNewAttemptState = remember { mutableStateOf(false) } // если мы хотим начать новую попытку до завершнения прошлой
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TestPreviewScreenTopBar { onBackPressed() } },
        bottomBar = {
            BottomNavigatorWithAttempt(
                newAttemptState = badNewAttemptState,
                courseManager = courseManager,
                navigateTo = navigateTo
            )
        }
    ) {
        if (attemptContent == null) {
            LoadErrorActivity()
            return@Scaffold
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
                text = "Попытки",
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
//            Text( // не используется
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

                if (attemptContent?.attempts == null) // FIXME нужно обработать случай
                    throw IllegalArgumentException("Попытки являются null")

                var index = 1
                for (item in attemptContent.attempts) {
                    AttemptsCard(
                        courseManager = courseManager,
                        chosenAttempt = courseManager.getAttemptId(),
                        idAttempt = item.id ?: -1,
                        numberCard = index++,
                        attemptStatus = item.state ?: "Ошибка",
                        date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT).format(Date((((item.timefinish?.toLong() ?: 0L) - 0).coerceAtLeast(0L) * 1000))).toString(),
                        navigateTo = navigateTo
                    )
                }
                if (badNewAttemptState.value) {
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            scaffoldState.snackbarHostState.showSnackbar("Попытка уже была начата")
                            badNewAttemptState.value = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttemptsCard(
    courseManager: CourseManager,
    chosenAttempt: MutableState<Int>,
    idAttempt: Int,
    numberCard: Int,
    attemptStatus: String,
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
                        text = numberCard.toString(),
                        fontFamily = FontFamily.Default
                    )
                    Column(
                        Modifier
                            .padding(start = 50.dp)
                            .width(120.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = when (attemptStatus) {
                                AttemptStatus.FINISHED.value -> "Завершённые"
                                AttemptStatus.IN_PROGRESS.value -> "В Процессе"
                                else -> "error"
                            },
                            style = MaterialTheme.typography.body1
                        )
                        if (attemptStatus == AttemptStatus.FINISHED.value) {
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
                        if (attemptStatus == AttemptStatus.FINISHED.value) {
                            courseManager.setTaskIndex(0)
                            courseManager.receiveQuizFinished(idAttempt.toString())
                            courseManager.setGlobalItem(true)
                        } else if (attemptStatus == AttemptStatus.IN_PROGRESS.value) {
                            courseManager.setTaskIndex(0)
                            courseManager.receiveQuizInProgress(idAttempt.toString(), "0")
                            courseManager.setGlobalItem(false)
                        }
                        chosenAttempt.value = idAttempt
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
    newAttemptState: MutableState<Boolean>,
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit
) {
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
//        BottomNavigationItem( // Назад
//            selected = selectedItem == 0,
//            onClick = {
//                // TODO сделать переход назад
//            },
//            icon = { Icon(imageVector = iconBack, contentDescription = null) },
//            label = { Text(textBack) }
//        )
        BottomNavigationItem( // Новая попытка
            selected = selectedItem == 1,
            onClick = {
                // FIXME Исправить метод добавления новой попытки
                if (courseManager.startNewAttempt((courseManager.getQuizAttemptContent()?.attempts?.getOrNull(0)?.quiz ?: -1).toString())) {
//                    attemptsState.add(courseManager.getQuizAttemptContent()?.attempts?.last())
                    newAttemptState.value = false
                } else {
                    newAttemptState.value = true
                }
            },
            icon = {
                Icon(
                    imageVector = iconAttempt,
                    contentDescription = null
                )
            },
            label = { Text(textAttempt) }
        )
//        BottomNavigationItem( // Вперёд
//            selected = selectedItem == 2,
//            onClick = {
//                // TODO сделать переход вперёд
//            },
//            icon = {
//                Icon(
//                    imageVector = iconForward,
//                    contentDescription = null
//                )
//            },
//            label = { Text(textForward) }
//        )
    }
}

package info.moevm.moodle.ui.coursecontent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.OnGloballyPositionedModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.rememberImeNestedScrollConnection
import info.moevm.moodle.data.courses.CoursesManager
import info.moevm.moodle.data.courses.exampleCourseContent
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescreen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.random

@OptIn(ExperimentalAnimatedInsets::class)
@Composable
fun TestScreen(
    coursesManager: CoursesManager,
    navigateTo: (Screen) -> Unit
) {
    if (coursesManager.requiredMoveLessonIndexForward) {
        coursesManager.requiredMoveLessonIndexForward = false
        coursesManager.moveLessonIndex(1)
    } else if (coursesManager.requiredMoveLessonIndexBack) {
        coursesManager.requiredMoveLessonIndexBack = false
        coursesManager.moveLessonIndex(-1)
    }
    val lessonContent = coursesManager.getTestLessonContent()
    val taskContent =
        lessonContent?.taskContent?.get(coursesManager.getAttemptKey().value)?.second?.get(
            coursesManager.getTaskContentItemIndexState().value
        ) as TestTaskContentItem?
    val taskState: MutableState<TaskStatus?> =
        remember { mutableStateOf(TaskStatus.NONE) }
    taskState.value = taskContent?.taskContentStatus

    val lazyListState = LazyListState(5)
    Scaffold(
        topBar = {
            TaskScreenTopBar(
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            Column {
                if (taskContent?.taskContentType == TaskContentType.TEST_ANSWER) {
                    Surface {
                        TestAnswer(taskAnswerType = taskContent.taskAnswerType, lazyListState = lazyListState)
                    }
                    Spacer(Modifier.height(12.dp))
                }
                BottomNavigatorWithChecker(
                    coursesManager = coursesManager,
                    taskState = taskState,
                    taskContentItemSize = coursesManager.getTestLessonContent()?.taskContent?.get(
                        coursesManager.getAttemptKey().value
                    )?.second?.size ?: 1,
                    navigateTo = navigateTo
                )
            }
        }
    ) { contentPadding ->
        // TODO Заблокировать отправку задания на проверку
        if (lessonContent == null || taskContent == null) {
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
        Column {
            LazyColumn(
                contentPadding = contentPadding,
                state = lazyListState,
                reverseLayout = true,
                modifier = Modifier
                    .nestedScroll(connection = rememberImeNestedScrollConnection())
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    when (taskContent.taskContentType) {
                        TaskContentType.TEST_ONE_CHOICE -> TestOneChoice(
                            taskAnswers = taskContent.taskAnswers
                        )
                        TaskContentType.TEST_MULTI_CHOICE -> TestMultiChoice(
                            taskAnswers = taskContent.taskAnswers
                        )
                        //                TaskContentType.TEST_ANSWER -> TestAnswer(taskAnswerType = taskContent.taskAnswerType)
                        TaskContentType.TEST_MATCH -> TestMatch(
                            taskAnswers = taskContent.taskAnswers,
                            taskAdditionInfo = taskContent.taskAdditionInfo
                        )
                        else -> {
                        }
                    }
                }
                item {
                    taskContent.taskContent()
                }
                item {
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        ),
                        text = if (taskContent.taskContentStatus == TaskStatus.DONE) "Выполнено" else "Не выполнено",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0f, 0f, 0f, 0.4f),
                            textAlign = TextAlign.Center
                        )
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 4.dp
                        ),
                        text = taskContent.taskMark,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0f, 0f, 0f, 0.4f),
                            textAlign = TextAlign.Center
                        )
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 6.dp
                        ),
                        text =
                        when (taskContent.taskContentType) {
                            TaskContentType.TEST_ONE_CHOICE -> "Выберите подходящий ответ из списка"
                            TaskContentType.TEST_MULTI_CHOICE -> "Выберите все подходящие ответы из списка"
                            TaskContentType.TEST_ANSWER -> "Введите ваш ответ на вопрос"
                            TaskContentType.TEST_MATCH -> "Расположите элементы в правильном порядке"
                            else -> "<error>"
                        },
                        style = MaterialTheme.typography.h6
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 10.dp
                        ),
                        text = taskContent.taskTitle,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigatorWithChecker(
    coursesManager: CoursesManager,
    taskState: MutableState<TaskStatus?>,
    taskContentItemSize: Int,
    navigateTo: (Screen) -> Unit
) {
    // TODO добавить изменение иконок при проверке
    val (iconBack, textBack) = when (coursesManager.getTaskContentItemIndexState().value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when (coursesManager.getTaskContentItemIndexState().value) {
        taskContentItemSize - 1 -> Pair(Icons.Filled.Task, "Завершить")
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
    val (iconStatus, textStatus) = when (taskState.value) {
        TaskStatus.NONE -> Pair(Icons.Filled.ArrowUpward, "Отправить")
        TaskStatus.DONE -> Pair(Icons.Filled.CheckCircle, "Верно")
        TaskStatus.FAILED -> Pair(Icons.Filled.Cached, "Повторить")
        else -> Pair(Icons.Filled.Error, "<error>")
    }
    val selectedItem by remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem( // Назад
            selected = selectedItem == 0,
            onClick = {
                if (coursesManager.getTaskContentItemIndexState().value == 0) {
                    coursesManager.requiredMoveLessonIndexBack = true
                    when (coursesManager.getPrevLessonType()) {
                        TaskType.TEST -> navigateTo(Screen.PreviewTest)
                        TaskType.TOPIC -> navigateTo(Screen.Article)
                        TaskType.NONE -> {
                        }
                    }
                } else
                    coursesManager.moveTaskIndex(-1)
            }, // FIXME исправить на нормально
            icon = { Icon(imageVector = iconBack, contentDescription = null) },
            label = { Text(textBack) }
        )
        BottomNavigationItem( // Статус проверки
            selected = selectedItem == 1,
            onClick = {
//                if (testChecker(
//                        courseContentItemIndex = courseContentItemIndex.value,
//                        lessonContentItemIndex = lessonContentItemIndex.value,
//                        testAttemptKey = testAttemptKey.value,
//                        taskContentItemIndex = taskContentItemIndex.value,
//
//                    ))
//
                if (taskState.value != TaskStatus.NONE)
                    taskState.value = TaskStatus.NONE
                else {
                    if (random() < 0.5)
                        taskState.value = TaskStatus.DONE
                    else
                        taskState.value = TaskStatus.FAILED
                }
                // TODO Проверка
            }, // FIXME исправить на нормально
            icon = {
                Icon(
                    imageVector = iconStatus,
                    tint = when (taskState.value) {
                        TaskStatus.DONE -> Color.Green
                        TaskStatus.FAILED -> Color.Red
                        else -> LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    },
                    contentDescription = null
                )
            },
            label = { Text(textStatus) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 2,
            onClick = {
                if (coursesManager.getTaskContentItemIndexState().value == taskContentItemSize - 1) {
                    coursesManager.requiredMoveLessonIndexForward = true
                    when (coursesManager.getNextLessonType()) {
                        TaskType.TEST -> navigateTo(Screen.PreviewTest)
                        TaskType.TOPIC -> navigateTo(Screen.Article)
                        TaskType.NONE -> {
                        }
                    }
                } else
                    coursesManager.moveTaskIndex(1)
            },
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

@Composable
fun TestTaskTitle(
    taskTitle: String
) {
    Column(
        Modifier.fillMaxSize()
    ) {
        Text(
            text = taskTitle,
            modifier = Modifier.padding(
                start = 30.dp,
                end = 30.dp,
                bottom = 5.dp
            ),
            style = MaterialTheme.typography.subtitle2
        )
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
        )
    }
}

@Composable
fun TestMatch(
    taskAnswers: List<String>,
    taskAdditionInfo: List<String>
) {
    val expandableState = remember { mutableStateOf(-1) }
    Column(Modifier.fillMaxSize()) {
        BoxWithConstraints(
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Варианты:",
                modifier = Modifier
                    .padding(start = 40.dp, bottom = 8.dp)
            )
        }
        for (i in taskAnswers.indices) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
            ) {
                BoxWithConstraints(Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = taskAdditionInfo[i],
                        modifier = Modifier
                            .padding(start = 30.dp, end = 12.dp)
                            .width(180.dp),
                        style = MaterialTheme.typography.body2
                    )
                }
                TestDropdownMenuItem(
                    id = i,
                    listOfOptions = taskAnswers,
                    expandableItemState = expandableState
                )
            }
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp)
            )
        }
    }
}

@Composable
fun TestDropdownMenuItem(
    id: Int,
    listOfOptions: List<String>,
    expandableItemState: MutableState<Int>
) {
    val textState = remember { mutableStateOf(listOfOptions.first()) }
    val textFieldFocus = remember { mutableStateOf(FocusRequester()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        TextField(
            modifier = Modifier.focusRequester(textFieldFocus.value),
            value = textState.value,
            onValueChange = {
                textState.value = it
            },
            trailingIcon = {
                Icon(
                    if (expandableItemState.value == id)
                        Icons.Filled.ArrowDropUp
                    else
                        Icons.Filled.ArrowDropDown,
                    null
                )
            },
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
            readOnly = true
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    expandableItemState.value = id
                    textFieldFocus.value.requestFocus()
                }
        )
        DropdownMenu(
            expanded = expandableItemState.value == id,
            onDismissRequest = { expandableItemState.value = -1 }
        ) {
            for (item in listOfOptions) {
                DropdownMenuItem(
                    onClick = {
                        textState.value = item
                        expandableItemState.value = -1
                    }
                ) {
                    Text(text = item)
                }
            }
        }
    }
}

@Composable
fun TestAnswer(
    taskAnswerType: TaskAnswerType,
    lazyListState: LazyListState
) {
    val stringState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsWithImePadding()
                .padding(start = 26.dp, end = 26.dp)
                .onFocusChanged {
                    if(it.isFocused) {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                },
            value = stringState.value,
            onValueChange = { stringState.value = it },
            placeholder = { Text("Ваш ответ", color = Color.LightGray) },
            textStyle = MaterialTheme.typography.body2,
            keyboardOptions = KeyboardOptions(
                keyboardType = when (taskAnswerType) {
                    TaskAnswerType.NUMBERS -> KeyboardType.Number
                    else -> KeyboardType.Text
                }
            )
        )
    }
}

@Composable
fun TestMultiChoice(
    taskAnswers: List<String>
) {
    val listCheckBoxState =
        MutableList(taskAnswers.size) { remember { mutableStateOf(false) } }
    Column(Modifier.selectableGroup()) {
        for (id in taskAnswers.indices) {
            TestCheckboxItem(
                checkboxItemState = listCheckBoxState[id],
                title = taskAnswers[id]
            )
        }
    }
}

@Composable
fun TestCheckboxItem(
    checkboxItemState: MutableState<Boolean>,
    title: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 30.dp)
            .clip(RoundedCornerShape(4.dp))
            .selectable(
                selected = checkboxItemState.value,
                onClick = {
                    checkboxItemState.value = !checkboxItemState.value
                },
                role = Role.Checkbox
            )
    ) {
        Checkbox(
            modifier = Modifier
                .padding(start = 4.dp, bottom = 10.dp, top = 10.dp)
                .align(Alignment.CenterVertically),
            checked = checkboxItemState.value,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
        )
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun TestOneChoice(
    taskAnswers: List<String>
) {
    val radioButtonState = remember { mutableStateOf(0) }
    Column(Modifier.selectableGroup()) {
        for (id in taskAnswers.indices) {
            TestRadioButtonItem(
                id = id,
                radioButtonItemState = radioButtonState,
                title = "${id + 1}. ${taskAnswers[id]}"
            )
        }
    }
}

@Composable
fun TestRadioButtonItem(
    id: Int,
    radioButtonItemState: MutableState<Int>,
    title: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 30.dp)
            .clip(RoundedCornerShape(4.dp))
            .selectable(
                selected = (radioButtonItemState.value == id),
                onClick = { radioButtonItemState.value = id },
                role = Role.RadioButton
            )
    ) {
        RadioButton(
            modifier = Modifier
                .padding(start = 4.dp, bottom = 10.dp, top = 10.dp)
                .align(Alignment.CenterVertically),
            selected = (radioButtonItemState.value == id),
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
        )
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

fun testChecker(
    courseContentItemIndex: Int,
    lessonContentItemIndex: Int,
    testAttemptKey: String,
    taskContentItemIndex: Int,
    listAnswer: List<String>
): Boolean {
    val courseData = exampleCourseContent().values.first()

    val rightAnswer = (
        (courseData[courseContentItemIndex].lessonContent[lessonContentItemIndex] as TestContentItems)
            .taskContent[testAttemptKey]!!.second[taskContentItemIndex] as TestTaskContentItem
        ).taskRightAnswers
    return rightAnswer == listAnswer
}

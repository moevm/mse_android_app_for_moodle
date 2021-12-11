package info.moevm.moodle.ui.lessoncontent

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.coursescontent.*
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalAnimatedInsets::class)
@Composable
fun TestScreen(
    courseManager: CourseManager,
    navigateTo: (Screen) -> Unit
) {
//    if (courseManager.requiredMoveLessonIndexForward) {
//        courseManager.requiredMoveLessonIndexForward = false
//        courseManager.moveLessonIndex(1)
//    } else if (courseManager.requiredMoveLessonIndexBack) {
//        courseManager.requiredMoveLessonIndexBack = false
//        courseManager.moveLessonIndex(-1)
//    }
    val testContentItem = courseManager.getTestTaskContentItem()
    val taskContentState = remember { mutableStateOf(testContentItem) }
//    val taskContent: TestTaskContentItem? = null
//    val taskState: MutableState<TaskStatus?> =
//        remember { mutableStateOf(TaskStatus.NONE) }
//    taskState.value = taskContent?.taskContentStatus

    Scaffold(
        topBar = {
            TaskScreenTopBar(
                courseManager = courseManager,
                onBack = { navigateTo(Screen.CourseContent) }
            )
        },
        bottomBar = {
            Column {
                if (testContentItem?.taskContentType == TaskContentType.TEST_FINISHED) {
                    BottomNavigatorWithChecker(
                        courseManager = courseManager,
//                        taskState = taskState,
                        navigatePrevPage = {
                            if (!courseManager.moveTaskIndex(-1))
                                navigateTo(Screen.TestAttempts)
                            courseManager.changeLocalTestItem()
                            taskContentState.value = courseManager.getTestTaskContentItem()
                        }
                    ) {
                        if (!courseManager.moveTaskIndex(1))
                            navigateTo(Screen.TestAttempts)
                        courseManager.changeLocalTestItem()
                        taskContentState.value =
                            courseManager.getTestTaskContentItem()
                    }
                } else if (testContentItem?.taskContentType == TaskContentType.TEST_IN_PROGRESS) {
                    BottomNavigatorWithChecker(
                        courseManager = courseManager,
//                        taskState = taskState,
                        navigatePrevPage = {
                            if (!courseManager.moveTaskIndex(-1))
                                navigateTo(Screen.TestAttempts)
                            courseManager.receiveQuizInProgress(courseManager.getAttemptKey().value.toString(), courseManager.getTaskContentItemIndexState().value.toString())
                            courseManager.changeLocalTestItem()
                            taskContentState.value = courseManager.getTestTaskContentItem()
                        }
                    ) {
                        if (!courseManager.moveTaskIndex(1))
                            navigateTo(Screen.TestAttempts)
                        courseManager.receiveQuizInProgress(
                            courseManager.getAttemptKey().value.toString(),
                            courseManager.getTaskContentItemIndexState().value.toString()
                        )
                        courseManager.changeLocalTestItem()
                        taskContentState.value =
                            courseManager.getTestTaskContentItem()
                    }
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            taskContentState.value!!.taskContent()
        }
    }
}

@Composable
fun BottomNavigatorWithChecker(
    courseManager: CourseManager,
//    taskState: MutableState<TaskStatus?>,
    navigatePrevPage: () -> Unit,
    navigateNextPage: () -> Unit
) {
    // TODO добавить изменение иконок при проверке
    val (iconBack, textBack) = when (courseManager.getTaskContentItemIndexState().value) {
        0 -> Pair(Icons.Filled.SubdirectoryArrowLeft, "Вернуться")
        else -> Pair(Icons.Filled.ChevronLeft, "Назад")
    }
    val (iconForward, textForward) = when {
        !courseManager.isRealPage(courseManager.getTaskContentItemIndexState().value+1) -> Pair(Icons.Filled.Task, "Завершить")
        else -> Pair(Icons.Filled.ChevronRight, "Далее")
    }
    val (iconStatus, textStatus) = /*when {
        TaskStatus.NONE -> */Pair(Icons.Filled.ArrowUpward, "Отправить")
        /*TaskStatus.DONE -> Pair(Icons.Filled.CheckCircle, "Верно")
        TaskStatus.FAILED -> Pair(Icons.Filled.Cached, "Повторить")
        else -> Pair(Icons.Filled.Error, "<error>")
    }*/
    val selectedItem by remember { mutableStateOf(0) }
    BottomNavigation {
        BottomNavigationItem( // Назад
            selected = selectedItem == 0,
            onClick = {
                navigatePrevPage()
            },
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
//                if (taskState.value != TaskStatus.NONE)
//                    taskState.value = TaskStatus.NONE
//                else {
//                    if (random() < 0.5)
//                        taskState.value = TaskStatus.DONE
//                    else
//                        taskState.value = TaskStatus.FAILED
//                }
                // TODO Проверка
            },
            icon = {
                Icon(
                    imageVector = iconStatus,
                    tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current) /*when (taskState.value) {
                        TaskStatus.DONE -> Color.Green
                        TaskStatus.FAILED -> Color.Red
                        else -> LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    }*/,
                    contentDescription = null
                )
            },
            label = { Text(textStatus) }
        )
        BottomNavigationItem( // Вперёд
            selected = selectedItem == 2,
            onClick = {
                navigateNextPage()
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
                    if (it.isFocused) {
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

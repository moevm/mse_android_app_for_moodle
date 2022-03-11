package info.moevm.moodle.ui.interests

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import info.moevm.moodle.R
import info.moevm.moodle.api.DataStoreUser
import info.moevm.moodle.data.courses.CourseManager
import info.moevm.moodle.data.courses.CoursesMap
import info.moevm.moodle.data.courses.CoursesRepository
import info.moevm.moodle.data.courses.AllCourseSelection
import info.moevm.moodle.ui.AppDrawer
import info.moevm.moodle.ui.Screen
import info.moevm.moodle.ui.components.LoadErrorActivity
import info.moevm.moodle.ui.signin.showMessage
import info.moevm.moodle.utils.produceUiState
import kotlinx.coroutines.launch

enum class Sections(val title: String) {
    AllCourse("Все курсы"),
    CurrentCourse("Ваши курсы"),
    Publications("Архив")
}

/**
 * TabContent for a single tab of the screen.
 *
 * This is intended to encapsulate a tab & it's content as a single object. It was added to avoid
 * passing several parameters per-tab from the stateful composable to the composable that displays
 * the current tab.
 *
 * @param section the tab that this content is for
 * @param section content of the tab, a composable that describes the content
 */
class TabContent(val section: Sections, val content: @Composable () -> Unit)

/**
 * Stateful InterestsScreen manages state using [produceUiState]
 *
 * @param navigateTo (event) request navigation to [Screen]
 * @param scaffoldState (state) state for screen Scaffold
 * @param coursesRepository data source for this screen
 */



@Composable
fun InterestsScreen(
    navigateTo: (Screen) -> Unit,
    coursesRepository: CoursesRepository,
    courseManager: CourseManager,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {

//    fun Context.lifecycleOwner(): LifecycleOwner? {
//        var curContext = this
//        var maxDepth = 20
//        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
//            curContext = (curContext as ContextWrapper).baseContext
//        }
//        return if (curContext is LifecycleOwner) {
//            curContext
//        } else {
//            null
//        }
//    }

    val context = LocalContext.current
//    val lifeSO = context.lifecycleOwner()
//    val dataStore = DataStoreUser(context)
    val tokenState: String = courseManager.getToken()

//    dataStore.tokenFlow.asLiveData().observe(
//        lifeSO!!,
//        {
////            tokenState = it
//        }
//    )

    // Returns a [CoroutineScope] that is scoped to the lifecycle of [InterestsScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope = rememberCoroutineScope()
    val changeCourse = { courseId: Int, courseName: String ->
        courseManager.setCourseId(courseId)
        courseManager.setCourseName(courseName)
        navigateTo(Screen.CourseContent)
    }
    val emptyChangerCourse = { _: Int, _: String -> showMessage(context, context.getString(R.string.overview_only)) }
    // Describe the screen sections here since each section needs 2 states and 1 event.
    // Pass them to the stateless InterestsScreen using a tabContent.
    val allCourseSection = TabContent(Sections.AllCourse) {
        val (allCourse) = produceUiState(coursesRepository) {
            getAllCourse(tokenState)
        }
        // collectAsState will read a [Flow] in Compose
        val selectedAllCourse by coursesRepository.observeAllCourseSelected()
            .collectAsState(setOf())
        val onAllCourseSelect: (AllCourseSelection) -> Unit = {
//            coroutineScope.launch { coursesRepository.toggleAllCourseSelection(it) }
        }
        val data = allCourse.value.data
        if (data != null && data.isEmpty()) {
            LoadErrorActivity()
        } else if(data != null) {
            AllCourseList(navigateTo, data, selectedAllCourse, onAllCourseSelect, emptyChangerCourse)
        }
    }

    val currentCourseSection = TabContent(Sections.CurrentCourse) {
        val (currentCourse) = produceUiState(coursesRepository) {
            getCurrentCourse(tokenState)
        }
        val selectedCurrentCourse by coursesRepository.observeCurrentCourseSelected()
            .collectAsState(setOf())
        val onCurrentCourseSelect: (String) -> Unit = {
            coroutineScope.launch { coursesRepository.toggleCurrentCourseSelected(it) }
        }
        val data = currentCourse.value.data // List<Pair<String, Int>>  -->  title + id
        if (data != null && data.isEmpty()) {
            LoadErrorActivity()
        } else if(data != null) {
            CurrentCourseList(navigateTo, data, selectedCurrentCourse, onCurrentCourseSelect, changeCourse)
        }
    }

//    val publicationSection = TabContent(Sections.Publications) { // Не требуется
//        val (publications) = produceUiState(coursesRepository) {
//            getPublications()
//        }
//        val selectedPublications by coursesRepository.observePublicationSelected()
//            .collectAsState(setOf())
//        val onPublicationSelect: (String) -> Unit = {
//            coroutineScope.launch {
//                coursesRepository.togglePublicationSelected(
//                    it
//                )
//            }
//        }
//        val data = publications.value.data ?: return@TabContent
//        PublicationList(
//            navigateTo,
//            data,
//            selectedPublications,
//            onPublicationSelect
//        )
//    }

    val tabContent = listOf(allCourseSection, currentCourseSection/*, publicationSection*/)
    val (currentSection, updateSection) = rememberSaveable {
        mutableStateOf(
            tabContent.first().section
        )
    }
    InterestsScreen(
        tabContent = tabContent,
        tab = currentSection,
        onTabChange = updateSection,
        navigateTo = navigateTo,
        scaffoldState = scaffoldState
    )
}

/**
 * Stateless interest screen displays the tabs specified in [tabContent]
 *
 * @param tabContent (slot) the tabs and their content to display on this screen, must be a non-empty
 * list, tabs are displayed in the order specified by this list
 * @param tab (state) the current tab to display, must be in [tabContent]
 * @param onTabChange (event) request a change in [tab] to another tab from [tabContent]
 * @param navigateTo (event) request navigation to [Screen]
 * @param scaffoldState (state) the state for the screen's [Scaffold]
 */
@Composable
fun InterestsScreen(
    tabContent: List<TabContent>,
    tab: Sections,
    onTabChange: (Sections) -> Unit,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState,
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Interests,
                closeDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                navigateTo = navigateTo
            )
        },
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topAppBarInterests"),
                title = { Text(stringResource(id = R.string.interest_activity_title)) },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("appDrawer"),
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_logo_light),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        TabContent(tab, navigateTo, onTabChange, tabContent)
    }
}

/**
 * Displays a tab row with [currentSection] selected and the body of the corresponding [tabContent].
 *
 * @param currentSection (state) the tab that is currently selected
 * @param updateSection (event) request a change in tab selection
 * @param tabContent (slot) tabs and their content to display, must be a non-empty list, tabs are
 * displayed in the order of this list
 */
@Composable
private fun TabContent(
    currentSection: Sections,
    navigateTo: (Screen) -> Unit,
    updateSection: (Sections) -> Unit,
    tabContent: List<TabContent>
) {
    val selectedTabIndex =
        tabContent.indexOfFirst { it.section == currentSection }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabContent.forEachIndexed { index, tabContent ->
                Tab(
                    text = { Text(tabContent.section.title) },
                    selected = selectedTabIndex == index,
                    onClick = { updateSection(tabContent.section) }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            // display the current tab content which is a @Composable () -> Unit
            tabContent[selectedTabIndex].content()
        }
    }
}

/**
 * Display the list for the topic tab
 *
 * @param courses (state) topics to display, mapped by section
 * @param selectedAllCourses (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 */
@Composable
private fun AllCourseList(
    navigateTo: (Screen) -> Unit,
    courses: CoursesMap,
    selectedAllCourses: Set<AllCourseSelection>,
    onTopicSelect: (AllCourseSelection) -> Unit,
    onCourseClicked: (Int, String) -> Unit
) {
    TabWithSections(navigateTo, courses, selectedAllCourses, onTopicSelect, onCourseClicked)
}

/**
 * Display the list for people tab
 *
 * @param people (state) people to display
 * @param selectedPeople (state) currently selected people
 * @param onPersonSelect (event) request a person selection be changed
 */
@Composable
private fun CurrentCourseList(
    navigateTo: (Screen) -> Unit,
    people: List<Pair<String, Int>>,
    selectedPeople: Set<String>,
    onPersonSelect: (String) -> Unit,
    onCourseClicked: (Int, String) -> Unit
) {
    TabWithTopics(navigateTo, people, selectedPeople, onPersonSelect, onCourseClicked)
}

/**
 * Display a list for publications tab
 *
 * @param publications (state) publications to display
 * @param selectedPublications (state) currently selected publications
 * @param onPublicationSelect (event) request a publication selection be changed
 */
// @Composable
// private fun PublicationList(
//     navigateTo: (Screen) -> Unit,
//     publications: List<String>,
//     selectedPublications: Set<String>,
//     onPublicationSelect: (String) -> Unit
// ) {
//     TabWithTopics(
//         navigateTo,
//         publications,
//         selectedPublications,
//         onPublicationSelect
//     )
// }

/**
 * Display a simple list of topics
 *
 * @param topics (state) topics to display
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 */
@Composable
private fun TabWithTopics(
    navigateTo: (Screen) -> Unit,
    topics: List<Pair<String, Int>>,
    selectedTopics: Set<String>,
    onTopicSelect: (String) -> Unit,
    onCourseClicked: (Int, String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .verticalScroll(scrollState)
    ) {
        for (index in topics.indices) {
            TopicItem(
                navigateTo = navigateTo,
                itemTitle = topics[index].first,
                selected = selectedTopics.contains(topics[index].first),
                itemIndex = topics[index].second,
                onCourseClicked = onCourseClicked
            ) { onTopicSelect(topics[index].first) }
            TopicDivider()
        }
    }
}

/**
 * Display a sectioned list of topics
 *
 * @param sections (state) topics to display, grouped by sections
 * @param selectedAllCourses (state) currently selected topics
 * @param onTopicSelect (event) request a topic+section selection be changed
 */
@Composable
private fun TabWithSections(
    navigateTo: (Screen) -> Unit,
    sections: CoursesMap,
    selectedAllCourses: Set<AllCourseSelection>,
    onTopicSelect: (AllCourseSelection) -> Unit,
    onCourseClicked: (Int, String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(vertical = 16.dp)) {
        sections.forEach { (section, topics) ->
//            Text(
//                text = section,
//                modifier = Modifier.padding(16.dp),
//                style = MaterialTheme.typography.subtitle1
//            )
            for (index in topics.indices) {
                TopicItem(
                    navigateTo = navigateTo,
                    itemTitle = topics[index].first,
                    selected = selectedAllCourses.contains(
                        AllCourseSelection(
                            section,
                            topics[index].first
                        )
                    ),
                    itemIndex = topics[index].second,
                    onCourseClicked = onCourseClicked
                ) { onTopicSelect(AllCourseSelection(section, topics[index].first)) }
                TopicDivider()
            }
        }
    }
}

/**
 * Display a full-width topic item
 *
 * @param itemTitle (state) topic title
 * @param selected (state) is topic currently selected
 * @param onToggle (event) toggle selection for topic
 */

@Composable
private fun TopicItem(
    navigateTo: (Screen) -> Unit,
    itemTitle: String,
    selected: Boolean,
    itemIndex: Int,
    onCourseClicked: (Int, String) -> Unit,
    onToggle: () -> Unit
) {
    val image = ImageBitmap.imageResource(R.drawable.placeholder_1_1)
    BoxWithConstraints {
        val boxScope = this
        Row(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = { onToggle() }
                )
                .clickable {
                    onCourseClicked(itemIndex, itemTitle)
//                    navigateTo(Screen.CourseContent)
                }
                .padding(horizontal = 16.dp)
        ) {
            Image(
                image,
                null,
                Modifier
                    .padding(
                        start = 8.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )
                    .align(Alignment.CenterVertically)
                    .size(56.dp, 56.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text(
                text = itemTitle,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(boxScope.maxWidth - 16.dp * 2 - 8.dp - 16.dp - 56.dp - 35.dp - 8.dp)
                    .padding(start = 16.dp, end = 8.dp),
                style = MaterialTheme.typography.subtitle1
            )
        // TODO: сделать переход на экран информации о курсе

            Box(
                modifier = Modifier
                    .clickable {
                    }
                    .size(42.dp)
                    .align(Alignment.CenterVertically)
            ) {
//                Icon(
//                    modifier = Modifier.align(Alignment.Center),
//                    imageVector = Icons.Outlined.Info,
//                    contentDescription = null
//                )
            }
        }
    }
}

/**
 * Full-width divider for topics
 */
@Composable
private fun TopicDivider() {
    Divider(
        modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp),
        color = MaterialTheme.colors.surface.copy(alpha = 0.08f)
    )
}
//
// @Preview("Interests screen")
// @Composable
// fun PreviewInterestsScreen() {
//     ThemedPreview {
//         InterestsScreen(
//             navigateTo = {},
//             coursesRepository = FakeCoursesRepository()
//         )
//     }
// }
//
// @Preview("Interests screen dark theme")
// @Composable
// fun PreviewInterestsScreenDark() {
//     ThemedPreview(darkTheme = true) {
//         val scaffoldState = rememberScaffoldState(
//             drawerState = rememberDrawerState(DrawerValue.Open)
//         )
//         InterestsScreen(
//             navigateTo = {},
//             scaffoldState = scaffoldState,
//             coursesRepository = FakeCoursesRepository()
//         )
//     }
// }
//
// @Preview("Interests screen drawer open")
// @Composable
// private fun PreviewDrawerOpen() {
//     ThemedPreview {
//         val scaffoldState = rememberScaffoldState(
//             drawerState = rememberDrawerState(DrawerValue.Open)
//         )
//         InterestsScreen(
//             navigateTo = {},
//             scaffoldState = scaffoldState,
//             coursesRepository = FakeCoursesRepository()
//         )
//     }
// }
//
// @Preview("Interests screen drawer open dark theme")
// @Composable
// private fun PreviewDrawerOpenDark() {
//     ThemedPreview(darkTheme = true) {
//         val scaffoldState = rememberScaffoldState(
//             drawerState = rememberDrawerState(DrawerValue.Open)
//         )
//         InterestsScreen(
//             navigateTo = {},
//             scaffoldState = scaffoldState,
//             coursesRepository = FakeCoursesRepository()
//         )
//     }
// }
//
// @Preview("Interests screen topics tab")
// @Composable
// fun PreviewTopicsTab(tokenState: String) {
//     ThemedPreview {
// //        TopicList(loadFakeTopics(tokenState), setOf(), {})
//     }
// }
//
// @Preview("Interests screen topics tab dark theme")
// @Composable
// fun PreviewTopicsTabDark(tokenState: String) {
//     ThemedPreview(darkTheme = true) {
// //        TopicList(loadFakeTopics(tokenState), setOf(), {})
//     }
// }
//
// @Composable
// private fun loadFakeTopics(tokenState: String): CoursesMap {
//     val topics = runBlocking {
//         FakeCoursesRepository().getTopics(tokenState)
//     }
//     return (topics as Result.Success).data
// }
//
// @Preview("Interests screen people tab")
// @Composable
// fun PreviewPeopleTab(tokenState: String) {
//     ThemedPreview {
// //        PeopleList(loadFakePeople(tokenState), setOf(), { })
//     }
// }
//
// @Preview("Interests screen people tab dark theme")
// @Composable
// fun PreviewPeopleTabDark(tokenState: String) {
//     ThemedPreview(darkTheme = true) {
// //        PeopleList(loadFakePeople(tokenState), setOf(), { })
//     }
// }
//
// @Composable
// private fun loadFakePeople(tokenState: String): List<String> {
//     val people = runBlocking {
//         FakeCoursesRepository().getPeople(tokenState)
//     }
//
//     return (people as Result.Success).data
// }
//
// @Preview("Interests screen publications tab")
// @Composable
// fun PreviewPublicationsTab() {
//     ThemedPreview {
// //        PublicationList(loadFakePublications(), setOf(), { })
//     }
// }
//
// @Preview("Interests screen publications tab dark theme")
// @Composable
// fun PreviewPublicationsTabDark() {
//     ThemedPreview(darkTheme = true) {
// //        PublicationList(loadFakePublications(), setOf(), { })
//     }
// }
//
// @Composable
// private fun loadFakePublications(): List<String> {
//     val publications = runBlocking {
//         FakeCoursesRepository().getPublications()
//     }
//     return (publications as Result.Success).data
// }
//
// @Preview("Interests screen tab with topics")
// @Composable
// fun PreviewTabWithTopics() {
//     ThemedPreview {
// //        TabWithTopics(topics = listOf("Hello", "Compose"), selectedTopics = setOf()) {}
//     }
// }
//
// @Preview("Interests screen tab with topics dark theme")
// @Composable
// fun PreviewTabWithTopicsDark() {
//     ThemedPreview(darkTheme = true) {
// //        TabWithTopics(topics = listOf("Hello", "Compose"), selectedTopics = setOf()) {}
//     }
// }

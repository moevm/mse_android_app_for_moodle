package info.moevm.moodle.data.courses

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import info.moevm.moodle.R
import info.moevm.moodle.ui.coursecontent.TestTaskTitle
import info.moevm.moodle.ui.coursescreen.*

fun exampleCourseContent(): CourseMapData {
    return mapOf(
        "Курс молодого бойца" to listOf(
            CourseContentItem(
                "Общие правила работы проектами",
                listOf(
                    null,
                    ArticleContentItems(
                        TaskType.TOPIC,
                        "Введение",
                        TaskStatus.NONE,
                        listOf(
                            ArticleTaskContentItem(
                                "Мы рады приветствовать вас в \"Курсе молодого бойца в IT\"!",
                                TaskContentType.ARTICLE,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "Данный курс является вводным для тех, кто хочет участвовать в IT-проектах в качестве исполнителя (разработчик, тестировщик). Из материалов курса вы узнаете:\n" +
                                        "\n" +
                                        "  *  Подходы к планированию проектной работы.\n" +
                                        "  *  Правила делового общения.\n" +
                                        "  *  Правила взаимодействия в проекте.\n" +
                                        "\n" +
                                        "Основным учебным материалом данного курса служит свод правил https://osllblog.wordpress.com/students/rules/"
                                )
                            },
                            ArticleTaskContentItem(
                                "Приветствие",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE
                            ) {
//                            Увеличение картнки при нажатии
//                            val scale = remember {mutableStateOf(2f)}
//                            val image = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3)
//                            val imageHeight = image.height * 2f
//                            val imageWidth = image.width * 2f
//                            Box(modifier = Modifier.height(imageHeight.dp).width(imageWidth.dp), contentAlignment = Alignment.TopCenter){
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
//                                    modifier = Modifier
//                                        .scale(scale.value)
//                                        .pointerInput(Unit) {
//                                            detectTapGestures(
//                                                onDoubleTap = {
//                                                    if(scale.value == 1f)
//                                                        scale.value = 2f
//                                                    else
//                                                        scale.value = 1f
//                                                }
//                                            )
//                                        }
//                                )
//                            }
                            },
                            ArticleTaskContentItem(
                                "Проект",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
                            },
                            ArticleTaskContentItem(
                                "Заказчик и требования",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
                            },
                            ArticleTaskContentItem(
                                "Итерации",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
                            },
                            ArticleTaskContentItem(
                                "Основные термины и определения",
                                TaskContentType.ARTICLE,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "Проект — комплекс мероприятий (задач), направленный на достижение какой-то конкретной цели, например: на создание уникального продукта, услуги, научного или иного результата. Как правило, целью проекта в ИТ является разработка программы, сервиса,  экспериментальное/теоретическое исследование закономерностей работы программ, алгоритмов или электронных устройств. \n" +
                                        "\n" +
                                        "Заказчик — лицо, заинтересованное в успешном завершении проекта и обладающее финансовыми или административными рычагами для определения процесса проектной работы. \n" +
                                        "\n" +
                                        "Требования заказчика / техническое задание — описание того, каким хочет видеть результат проекта его заказчик. Могут определять как качественные требования к работе программы (например, обязательно использовать Python, программа должна быть опубликована в магазине приложений) или результату исследования (например, необходимо подготовить научную статью по результатам), так и количественные характеристики последних (например, временная сложность алгоритма не более квадратичной). Что важно понимать про требования:\n" +
                                        "\n" +
                                        "    Требования почти всегда меняются в процессе работы над проектом, так как, по мере погружения в предметную область, появляются дополнительные сведения позволяющие уточнить исходные постановки\n" +
                                        "    На начальном этапе выполнения проекта требования имеют минимум деталей.\n" +
                                        "\n" +
                                        "Критерий успешности проекта складывается из соответствия результатов требованиям заказчика и качества взаимодействия команды с заказчиком.\n" +
                                        "\n" +
                                        "Участники проекта — лица, выполняющие задачи проекта. В число участников, как правило, входят как разработчики (программисты, тестировщики, дизайнеры), так и менеджеры - люди, занятые выполнением административных задач (проверка выполнения задач, долгосрочное планирование, контроль исполнения).\n" +
                                        "\n" +
                                        "Трекер — система управления задачами, помогающая структурировать работу, отслеживать текущий статус проекта и планировать работу.\n" +
                                        "\n" +
                                        "Итерация проекта — отдельный временной промежуток работы над проектом. Как правило, варьируется от одного дня до недели. "
                                )
                            },
                            ArticleTaskContentItem(
                                "Атомарность задач",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
                            },
                            ArticleTaskContentItem(
                                "Процесс проектной работы",
                                TaskContentType.ARTICLE,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "Процесс проектной работы строится на базе повторяющихся с заданной периодичностью итераций. Завершение очередной итерации  сопровождается встречей (либо онлайн-синхронизацией через Skype, Zoom и т.д.) участников проекта. Встреча подразумевает для каждого из участников ответ на 3 вопроса:\n" +
                                        "\n" +
                                        "    что было сделано на в рамках завершенной итерации\n" +
                                        "    что будет сделано в течение следующей итерации;\n" +
                                        "    какие есть сложности и вопросы, блокирующие выполнение работы\n" +
                                        "\n" +
                                        "После встречи участник начинает работу над задачами из плана. Завершенные задачи участник отправляет на проверку лидеру команды. Лидер команды выполняет проверку готовности и качества решений, а также производит их интеграцию в основную ветку репозитория. Параллельно с этим, лидер команды также взаимодействует с заказчиком: уточняет требования и предъявляет результаты."
                                )
                            }
                        )
                    ),
                    ArticleContentItems(
                        TaskType.TOPIC,
                        "Автобусный фактор (bus factor)",
                        TaskStatus.NONE,
                        listOf(
                            ArticleTaskContentItem(
                                "Busfactor",
                                TaskContentType.VIDEO,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Image(
                                    bitmap = ImageBitmap.imageResource(id = R.drawable.placeholder_4_3),
                                    contentDescription = null
                                )
                            },
                            null,
                            ArticleTaskContentItem(
                                "Автобусный фактор",
                                TaskContentType.ARTICLE,
                                "К настоящему времени Вы заработали баллов: 0 из 0 возможных.",
                                TaskStatus.DONE,
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = "В IT-проектах критическое значение имеет обмен информацией между участниками. Это связано со следующими факторами:\n" +
                                        "\n" +
                                        "    проектные роли часто подразумевают значительную специализацию (например, ASM-frontend разработчик) и требуют длительного обучения;\n" +
                                        "    в процессе разработки с высокой скоростью генерируются большие объемы информации, которые необходимо максимально оперативно доводить до сведения команды, например:\n" +
                                        "        актуальный статус разработки,\n" +
                                        "        особенности работы ПО,\n" +
                                        "        архитектура,\n" +
                                        "        типовые последовательности действий,\n" +
                                        "        инструкции по использованию,\n" +
                                        "        информация о зависимостях ПО.\n" +
                                        "\n" +
                                        "Скорость и качество информационного обмена напрямую определяет успешность проекта. Чем сложнее и медленнее происходит обмен, тем выше вероятность снижения качества разрабатываемого ПО. \n" +
                                        "\n" +
                                        "Качество информационного обмена определяется:\n" +
                                        "\n" +
                                        "    навыками взаимодействия участников,\n" +
                                        "    инструментами накопления знаний (вики, документация),\n" +
                                        "    инструментами взаимодействия (чаты, электронная почта, личное общение).\n" +
                                        "\n" +
                                        "Для косвенной оценки уровня информационного обмена применяется такая величина как автобусный фактор - минимальное количество участников проекта, выбывание которых приведет к остановке работы. \n" +
                                        "\n" +
                                        "К снижению автобусного фактора (ухудшению качества коммуникации) приводят:\n" +
                                        "\n" +
                                        "    незаменимые специалисты;\n" +
                                        "    тайные знания  - ситуация, когда знанием о каком-то аспекте работы продукта обладает ограниченное количество участников;\n" +
                                        "    тайный код - ситуация, когда фрагменты кода есть только на локальных компьютерах разработчиков и  отсутствуют в основном репозитории проекта;\n" +
                                        "\n" +
                                        "Как повысить автобусный фактор?\n" +
                                        "\n" +
                                        "    оперативно загружать в репозиторий любые, даже самые малые изменения в исходном коде, в том числе\n" +
                                        "         промежуточные результаты,\n" +
                                        "        эксперименты;\n" +
                                        "    документировать работу ПО;\n" +
                                        "    тщательно вести задачи в трекере;\n" +
                                        "    максимально подробно описывать проблемы при взаимодействии с коллегами.\n" +
                                        "\n" +
                                        "Задача каждого участника проекта - всеми силами повышать автобусный фактор!"
                                )
                            }
                        )
                    ),
                    TestContentItems(
                        TaskType.TEST,
                        "Общие правила работы проектами. Тест",
                        TaskStatus.WORKING,
                        "Метод оценивания: Высшая оценка",
                        hashMapOf(
                            "0" to Pair(
                                AttemptData(
                                    0,
                                    "Отправлено 19.07.2021, 10:45",
                                    TaskStatus.DONE
                                ),
                                listOf(
                                    TestTaskContentItem(
                                        "Вопрос 1",
                                        TaskContentType.TEST_ONE_CHOICE,
                                        "Баллов: 0 из 1",
                                        TaskStatus.NONE,
                                        taskAnswers = listOf(
                                            "Тайный кот",
                                            "Ненавидимый стажер",
                                            "Обмен информацией",
                                            "Информационный обмен",
                                            "Тайные знания",
                                            "Тайный код"
                                        ),
                                        taskRightAnswers = listOf("Тайный код")
                                    ) {
                                        TestTaskTitle(
                                            taskTitle = "Стажер в компании \"Crutches and bicycles\" весь день работал над фиксом к задаче AAA-234565. По завершению рабочего дня было написано примерно 50% от необходимого кода. Свои наработки стажер не загружал в основной репозиторий.\n" +
                                                "\n" +
                                                "Какое явление из предыдущего степа воспроизвел стажер?",
                                        )
                                    },
                                    TestTaskContentItem(
                                        "Вопрос 2",
                                        TaskContentType.TEST_MULTI_CHOICE,
                                        "Баллов: 1 из 1",
                                        TaskStatus.NONE,
                                        taskAnswers = listOf(
                                            "Требование определяет исполнитель",
                                            "Успешность проекта определяется соответствием требованиям",
                                            "Требования определяет заказчик",
                                            "Требования не могут измениться в ходе работы",
                                            "Требования изменятся в ходе работы",
                                            "Успешность проекта не определяется соответствием требованиям"
                                        ),
                                        taskRightAnswers = listOf(
                                            "Успешность проекта определяется соответствием требованиям",
                                            "Требования определяет заказчик",
                                            "Требования изменятся в ходе работы"
                                        ),
                                        taskContent = {
                                            TestTaskTitle(
                                                taskTitle = "Выберите один или несколько ответов:"
                                            )
                                        }
                                    ),
                                    TestTaskContentItem(
                                        "Вопрос 3",
                                        TaskContentType.TEST_ANSWER,
                                        "Баллов: 0 из 1",
                                        TaskStatus.NONE,
                                        taskAnswers = listOf(),
                                        taskRightAnswers = listOf("1"),
                                        taskAnswerType = TaskAnswerType.NUMBERS,
                                        taskContent = {
                                            TestTaskTitle(
                                                taskTitle = "В компании \"Crutches and bicycles\" разрабатывается веб-приложение для оценки эффективности программистов. \n" +
                                                    "\n" +
                                                    "Команда разработчиков продукта состоит из следующих людей:\n" +
                                                    "\n" +
                                                    "Иван, frontend developer.\n" +
                                                    "Сергей, frontend developer.\n" +
                                                    "Екатерина, fullstack developer.\n" +
                                                    "\n" +
                                                    "\n\n\n\n\n\n\n" +
                                                    "Для успешной работы проекта необходимо решать как frontend, так и backend задачи. \n" +
                                                    "\n" +
                                                    "Чему равен bus factor данной команды? "
                                            )
                                        }
                                    ),
                                    TestTaskContentItem(
                                        "Вопрос 4",
                                        TaskContentType.TEST_MATCH,
                                        "Баллов: 0 из 1",
                                        TaskStatus.NONE,
                                        taskAnswers = listOf(
                                            "|3.842|",
                                            "|3.842e+00|asdkldgnalkg;ljamjknMGALJNlsdGALSKGN",
                                            "|3.84|",
                                            "|3.841500|",
                                            "|3.8415|"
                                        ),
                                        taskAdditionInfo = listOf(
                                            "printf(\"|%g|\\n\", f);",
                                            "printf(\"|%1.2f|\\n\", f);\n",
                                            "printf(\"|%.3e|\\n\", f);",
                                            "printf(\"|%3.3f|\\n\", f);",
                                            "printf(\"|%f|\\n\", f);"
                                        ),
                                        taskRightAnswers = listOf(
                                            "|3.8415|",
                                            "|3.84|",
                                            "|3.842e+00|",
                                            "|3.842|",
                                            "|3.841500|"
                                        ),
                                        taskContent = {
                                            TestTaskTitle(
                                                taskTitle = "Сопоставьте инструкцию и результат, который будет выведен на экран, при условии, что\n" +
                                                    "\n" +
                                                    "float f = 3.8415;"
                                            )
                                        }
                                    ),
                                )
                            ),
                            "1" to Pair(
                                AttemptData(1, "", TaskStatus.DONE),
                                listOf()
                            )
                        )
                    )
                )
            ),
            CourseContentItem(
                "Планирование календаря",
                listOf(
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Как планировать календарь работы",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Планирование и оценка времени выполнения задач",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TEST,
                        "Планирование календаря. Тест",
                        TaskStatus.NONE
                    )
                )
            ),
            CourseContentItem(
                "Коммуникация по проекту",
                listOf(
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Решение оперативных задач",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Как писать письма",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Описание проблем в письмах",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Протоколы встреч",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TEST,
                        "Коммуникация по проекту. Тест",
                        TaskStatus.WORKING
                    ),
                )
            ),
            CourseContentItem(
                "Работа с задачами",
                listOf(
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Трекеры задач",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Наблюдаемые результаты задач",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Декомпозиция задач",
                        TaskStatus.NONE
                    ),
                    LessonContentItem(
                        TaskType.TEST,
                        "Работа с задачами. Тест",
                        TaskStatus.DONE
                    ),
                    LessonContentItem(
                        TaskType.TOPIC,
                        "Прощание",
                        TaskStatus.NONE
                    ),
                )
            )
        )
    )
}

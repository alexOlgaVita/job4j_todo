package ru.job4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dto.TaskDto;
import ru.job4j.model.Category;
import ru.job4j.model.Priority;
import ru.job4j.model.TodoUser;
import ru.job4j.service.category.CategoryService;
import ru.job4j.service.priority.PriorityService;
import ru.job4j.service.task.TaskService;
import ru.job4j.service.todouser.TodoUserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.converter.ConverterDateTime.getDate;
import static ru.job4j.converter.ConverterDateTime.getLocalDateTimeFromString;

@SpringBootTest
@AutoConfigureMockMvc
class TasksControllerTest {

    private static final String date1 = "07.08.2025 06:00:01";
    private static final String date2 = "07.09.2025 06:00:01";
    private static final String date3 = "12.11.2025 15:03:02";
    private static final TodoUser user = new TodoUser(null, "Ольга", "olga", "pass", "Europe/Paris");
    private static Priority priority = new Priority();
    private static List<Category> categories = new ArrayList<>();
    private TaskService taskService;
    private PriorityService priorityService;
    private CategoryService categoryService;
    private TodoUserService todoUserService;
    private TaskController taskController;
    private MockHttpSession session;
    private String zonedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void initServices() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockMvc).build();
        session = new MockHttpSession();
        taskService = mock(TaskService.class);
        priorityService = mock(PriorityService.class);
        categoryService = mock(CategoryService.class);
        todoUserService = mock(TodoUserService.class);
        taskController = new TaskController(taskService, priorityService, categoryService, todoUserService);
    }

    @Test
    public void whenRequestTaskAllListPageThenGetPageWithAllTasks() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", getLocalDateTimeFromString(date1),
                getDate(getLocalDateTimeFromString(date1)), false, user, priority, categories,
                getLocalDateTimeFromString(date1).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", getLocalDateTimeFromString(date2),
                getDate(getLocalDateTimeFromString(date2)), true, user, priority, categories,
                getLocalDateTimeFromString(date2).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var expectedTasks = List.of(taskDto1, taskDto2);
        when(taskService.findAll()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAll(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenRequestTaskDoneListPageThenGetPageWitDoneTasks() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", getLocalDateTimeFromString(date1),
                getDate(getLocalDateTimeFromString(date1)), false, user, priority, categories,
                getLocalDateTimeFromString(date1).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", getLocalDateTimeFromString(date2),
                getDate(getLocalDateTimeFromString(date2)), true, user, priority, categories,
                getLocalDateTimeFromString(date2).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var taskDto3 = new TaskDto(2, "Бадминтон", "Поиграть с другом в бадминтон", getLocalDateTimeFromString(date3),
                getDate(getLocalDateTimeFromString(date3)), true, user, priority, categories,
                getLocalDateTimeFromString(date3).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var expectedTasks = List.of(taskDto2, taskDto3);
        when(taskService.findAllDone()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAllDone(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenRequestTaskNewListPageThenGetPageWitNewTasks() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", getLocalDateTimeFromString(date1),
                getDate(getLocalDateTimeFromString(date1)), false, user, priority, categories,
                getLocalDateTimeFromString(date1).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", getLocalDateTimeFromString(date2),
                getDate(getLocalDateTimeFromString(date2)), true, user, priority, categories,
                getLocalDateTimeFromString(date2).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var taskDto3 = new TaskDto(2, "Бадминтон", "Поиграть с другом в бадминтон", getLocalDateTimeFromString(date3),
                getDate(getLocalDateTimeFromString(date3)), false, user, priority, categories,
                getLocalDateTimeFromString(date3).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        var expectedTasks = List.of(taskDto1, taskDto3);
        when(taskService.findAllNew()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAllNew(model);
        var actualTasks = model.getAttribute("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenRequestTaskCreationPageThenGetPage() {
        var priority1 = new Priority(null, "Срочный", 1);
        var priority2 = new Priority(null, "Средний", 2);
        var expectedPriorities = List.of(priority1, priority2);
        when(priorityService.findAll()).thenReturn(expectedPriorities);

        var model = new ConcurrentModel();
        var view = taskController.getCreationPage(model);
        var actualTask = model.getAttribute("task");
        var actualPriorities = model.getAttribute("priorities");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(actualTask).isEqualTo(null);
        assertThat(actualPriorities).isEqualTo(expectedPriorities);
    }

    @Test
    public void whenRequestTaskByIdPageThenGetPageWithTask() {
        var taskDto = new TaskDto(1, "Кино", "Сходить в кино с друзьями", getLocalDateTimeFromString(date1),
                getDate(getLocalDateTimeFromString(date1)), false, user, priority, categories,
                getLocalDateTimeFromString(date1).atZone(ZoneId.of(user.getTimezone())).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss Z")));
        when(taskService.findById(taskDto.getId())).thenReturn(Optional.of(taskDto));

        var model = new ConcurrentModel();
        var view = taskController.getCreationPage(model, taskDto.getId());
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(taskDto);
    }

    @Test
    public void whenRequestDoDoneTaskByIdPageThenDoneAndToSuccessPage() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", getLocalDateTimeFromString(date1),
                getDate(getLocalDateTimeFromString(date1)), false, user, priority, categories, null);
        when(taskService.setDone(taskDto1.getId())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.setDonePage(taskDto1, taskDto1.getId(), model);

        assertThat(view).isEqualTo("tasks/success");
    }
}
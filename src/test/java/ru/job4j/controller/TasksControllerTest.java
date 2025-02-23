package ru.job4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dto.TaskDto;
import ru.job4j.service.task.TaskService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TasksControllerTest {

    private static final String date1 = "2025-08-07 06:00:01";
    private static final String date2 = "2025-09-07 06:00:01";
    private static final String date3 = "2025-11-12 15:03:02";


    private TaskService taskService;
    private TaskController taskController;
    private MockHttpSession session;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void initServices() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockMvc).build();
        session = new MockHttpSession();
        taskService = mock(TaskService.class);
        taskController = new TaskController(taskService);
    }

    @Test
    public void whenRequestTaskAllListPageThenGetPageWithAllTasks() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", Timestamp.valueOf(date2),
                new Date(Timestamp.valueOf(date2).getTime()), true);
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
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", Timestamp.valueOf(date2),
                new Date(Timestamp.valueOf(date2).getTime()), true);
        var taskDto3 = new TaskDto(2, "Бадминтон", "Поиграть с другом в бадминтон", Timestamp.valueOf(date3),
                new Date(Timestamp.valueOf(date3).getTime()), true);
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
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(2, "Прогулка", "Прогуляться по парку", Timestamp.valueOf(date2),
                new Date(Timestamp.valueOf(date2).getTime()), true);
        var taskDto3 = new TaskDto(2, "Бадминтон", "Поиграть с другом в бадминтон", Timestamp.valueOf(date3),
                new Date(Timestamp.valueOf(date3).getTime()), false);
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
        var model = new ConcurrentModel();
        var view = taskController.getCreationPage();
        var actualTask = model.getAttribute("task");
        assertThat(view).isEqualTo("tasks/create");
        assertThat(actualTask).isEqualTo(null);
    }

    @Test
    public void whenPostTaskCreationThenSameDataAndToSuccessPage() throws Exception {
        var taskDto = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskArgumentCaptor = ArgumentCaptor.forClass(TaskDto.class);
        when(taskService.create(taskArgumentCaptor.capture())).thenReturn(taskDto);

        var model = new ConcurrentModel();
        var view = taskController.create(taskDto, model);
        var actualTask = taskArgumentCaptor.getValue();

        assertThat(view).isEqualTo("tasks/success");
        assertThat(actualTask).isEqualTo(taskDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Impossible to create the task");
        when(taskService.create(any())).thenReturn(null);

        var model = new ConcurrentModel();
        var view = taskController.create(new TaskDto(), model);
        var actualError = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualError).isEqualTo("Возникла ошибка при создании задания");
    }

    @Test
    public void whenRequestTaskByIdPageThenGetPageWithTask() {
        var taskDto = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        when(taskService.findById(taskDto.getId())).thenReturn(Optional.of(taskDto));

        var model = new ConcurrentModel();
        var view = taskController.getCreationPage(model, taskDto.getId());
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(taskDto);
    }

    @Test
    public void whenPostUpdateTaskWithFileThenSameDataAndRedirectToSuccessPage() throws Exception {
        var taskDto = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        var taskArgumentCaptor = ArgumentCaptor.forClass(TaskDto.class);
        when(taskService.update(taskArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.update(taskDto, taskDto.getId(), model);
        var actualTask = taskArgumentCaptor.getValue();

        assertThat(view).isEqualTo("tasks/success");
        assertThat(actualTask).isEqualTo(taskDto);
    }

    @Test
    public void whenRequestDeleteTaskByIdPageThenDeleteAndRedirectPageWithTasks() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date2),
                new Date(Timestamp.valueOf(date2).getTime()), false);
        when(taskService.findById(taskDto1.getId())).thenReturn(Optional.of(taskDto1));
        when(taskService.delete(taskDto1.getId())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.delete(taskDto1, taskDto1.getId(), model);

        assertThat(view).isEqualTo("redirect:/tasks");
    }

    @Test
    public void whenRequestDoDoneTaskByIdPageThenDoneAndToSuccessPage() {
        var taskDto1 = new TaskDto(1, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        when(taskService.setDone(taskDto1.getId())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.setDonePage(taskDto1, taskDto1.getId(), model);

        assertThat(view).isEqualTo("tasks/success");
    }
}
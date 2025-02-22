package ru.job4j.service.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.dto.TaskDto;
import ru.job4j.mapper.TaskMapper;
import ru.job4j.store.TaskStore;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class SimpleTaskServiceTest {

    private static final String date1 = "2025-08-07 06:00:01";

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void whenCreateSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        TaskStore taskRepositoryMock = mock(TaskStore.class);
        when(taskRepositoryMock.create(taskMapper.getEntityFromModelCustom(taskDto1))).thenReturn(taskMapper.getEntityFromModelCustom(taskDto1));
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        TaskDto savedTask = simpleTaskService.create(taskDto1);
        assertThat(savedTask).isEqualTo(taskDto1);
    }

    @Test
    void whenCreateFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        TaskStore taskRepositoryMock = mock(TaskStore.class);
        when(taskRepositoryMock.create(taskMapper.getEntityFromModelCustom(taskDto1))).thenReturn(null);
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        TaskDto savedTask = simpleTaskService.create(taskDto1);
        assertThat(savedTask).isNull();
    }

    @Test
    void whenUpdateSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        TaskStore taskRepositoryMock = mock(TaskStore.class);
        when(taskRepositoryMock.update(taskMapper.getEntityFromModelCustom(taskDto1))).thenReturn(true);
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        assertThat(simpleTaskService.update(taskDto1)).isTrue();
    }

    @Test
    void whenUpdateFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        TaskStore taskRepositoryMock = mock(TaskStore.class);
        when(taskRepositoryMock.update(taskMapper.getEntityFromModelCustom(taskDto1))).thenReturn(false);
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        TaskDto savedTask = simpleTaskService.create(taskDto1);
        assertThat(simpleTaskService.update(taskDto1)).isFalse();
    }

    @Test
    void whenFindByIdSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findById(taskDto1.getId())).thenReturn(Optional.ofNullable(taskMapper.getEntityFromModelCustom(taskDto1)));

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        Optional<TaskDto> foundById =
                simpleTaskService.findById(taskDto1.getId());

        assertThat(foundById.get()).isEqualTo(taskDto1);
    }

    @Test
    void whenFindByIdFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findById(taskDto1.getId())).thenReturn(Optional.empty());

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        Optional<TaskDto> foundById =
                simpleTaskService.findById(taskDto1.getId());

        assertThat(foundById).isEmpty();
    }

    @Test
    void whenDeleteByIdSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock.delete(taskDto1.getId())).thenReturn(true);
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        assertThat(simpleTaskService.delete(taskDto1.getId())).isTrue();
    }

    @Test
    void whenDeleteByIdFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock.delete(taskDto1.getId())).thenReturn(false);
        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        assertThat(simpleTaskService.delete(taskDto1.getId())).isFalse();
    }

    @Test
    void whenFindAllSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAll()).thenReturn((List.of(taskMapper.getEntityFromModelCustom(taskDto1),
                taskMapper.getEntityFromModelCustom(taskDto2))));

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAll = (List<TaskDto>) simpleTaskService.findAll();

        assertThat(foundAll).isEqualTo(List.of(taskDto1, taskDto2));
    }

    @Test
    void whenFindAllFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAll()).thenReturn(null);

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAll = (List<TaskDto>) simpleTaskService.findAll();

        assertThat(foundAll).isEmpty();
    }

    @Test
    void whenFindAllDoneSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);
        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto3 = new TaskDto(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAllDone()).thenReturn((List.of(taskMapper.getEntityFromModelCustom(taskDto3),
                taskMapper.getEntityFromModelCustom(taskDto1))));

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAllDone = (List<TaskDto>) simpleTaskService.findAllDone();

        assertThat(foundAllDone).isEqualTo(List.of(taskDto3, taskDto1));
    }

    @Test
    void whenFindAllDoneFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto3 = new TaskDto(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAllDone()).thenReturn(null);

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAllDone = (List<TaskDto>) simpleTaskService.findAllDone();

        assertThat(foundAllDone).isEmpty();
    }

    @Test
    void whenFindAllNewSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);
        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);
        var taskDto3 = new TaskDto(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAllNew()).thenReturn((List.of(taskMapper.getEntityFromModelCustom(taskDto3),
                taskMapper.getEntityFromModelCustom(taskDto1))));

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAllNew = (List<TaskDto>) simpleTaskService.findAllNew();

        assertThat(foundAllNew).isEqualTo(List.of(taskDto3, taskDto1));
    }

    @Test
    void whenFindAllNewFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);
        var taskDto2 = new TaskDto(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);
        var taskDto3 = new TaskDto(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), true);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findAllNew()).thenReturn(null);

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        List<TaskDto> foundAllNew = (List<TaskDto>) simpleTaskService.findAllNew();

        assertThat(foundAllNew).isEmpty();
    }

    @Test
    void whenFindByNameSuccessfullyWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findByName(taskDto1.getName())).thenReturn(Optional.ofNullable(taskMapper.getEntityFromModelCustom(taskDto1)));

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        Optional<TaskDto> foundByName =
                simpleTaskService.findByName(taskDto1.getName());

        assertThat(foundByName.get()).isEqualTo(taskDto1);
    }

    @Test
    void whenFindByNameFailWithMock() {
        var taskDto1 = new TaskDto(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1),
                new Date(Timestamp.valueOf(date1).getTime()), false);

        TaskStore taskRepositoryMock = mock(TaskStore.class);

        when(taskRepositoryMock
                .findByName(taskDto1.getName())).thenReturn(Optional.empty());

        TaskService simpleTaskService = new SimpleTaskService(taskRepositoryMock, taskMapper);
        Optional<TaskDto> findByName =
                simpleTaskService.findByName(taskDto1.getName());

        assertThat(findByName).isEmpty();
    }
}
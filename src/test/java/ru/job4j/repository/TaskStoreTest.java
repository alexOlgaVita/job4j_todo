package ru.job4j.repository;


import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.model.Task;
import ru.job4j.store.TaskStore;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskStoreTest {

    private static final String date1 = "2025-08-07 06:00:01";
    private static final String date2 = "2025-09-07 06:00:01";
    private static TaskStore taskStore;

    @BeforeAll
    public static void initRepositories() throws Exception {

        SessionFactory sf;
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml").build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .addAnnotatedClass(Task.class)
                    .getMetadataBuilder()
                    .build();

            sf = metadata.getSessionFactoryBuilder().build();

        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
        taskStore = new TaskStore(sf);
    }

    @AfterEach
    public void clearTasksAfter() {
        clearTasks();
    }

    @BeforeEach
    public void clearTasksBefore() {
        clearTasks();
    }

    private void clearTasks() {
        var tasks = taskStore.findAll();
        for (var task : tasks) {
            taskStore.delete(task.getId());
        }
    }

    @Test
    public void whenCreateThenGetSame() {
        var task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false);
        var taskCreated = taskStore.create(task);
        var createdTask = taskStore.findById(taskCreated.getId());
        assertThat(createdTask).usingRecursiveComparison().isEqualTo(Optional.ofNullable(taskCreated));
    }

    @Test
    public void whenCreateSeveralThenGetAll() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true));
        var task2 = taskStore.create(new Task(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), true));
        var task3 = taskStore.create(new Task(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1), true));
        var result = taskStore.findAll();
        assertThat(result).isEqualTo(List.of(task1, task2, task3));
    }

    @Test
    public void whenCreateSeveralThenGetAllDone() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true));
        var task2 = taskStore.create(new Task(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), false));
        var task3 = taskStore.create(new Task(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1), true));
        var result = taskStore.findAllDone();
        assertThat(result).isEqualTo(List.of(task1, task3));
    }

    @Test
    public void whenCreateSeveralThenGetAllNew() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false));
        var task2 = taskStore.create(new Task(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), true));
        var task3 = taskStore.create(new Task(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1), false));
        var result = taskStore.findAllNew();
        assertThat(result).isEqualTo(List.of(task1, task3));
    }

    @Test
    public void whenTestFindByNameIsSuchTask() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false));
        var task2 = taskStore.create(new Task(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), true));
        var task3 = taskStore.create(new Task(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1), false));
        var result = taskStore.findByName(task2.getName());
        assertThat(result).isEqualTo(Optional.ofNullable(task2));
    }

    @Test
    public void whenTestFindByNameIsNotSuchTask() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false));
        var task2 = taskStore.create(new Task(7, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), true));
        var task3 = taskStore.create(new Task(1, "Английский", "Выполнить задание по английскомй", Timestamp.valueOf(date1), false));
        var result = taskStore.findByName("Отпуск");
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(taskStore.findAll()).isEqualTo(emptyList());
        assertThat(taskStore.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var task1 = taskStore.create(new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true));
        var isDeleted = taskStore.delete(task1.getId());
        var createdTask = taskStore.findById(task1.getId());
        assertThat(isDeleted).isTrue();
        assertThat(createdTask).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetTrue() {
        assertThat(taskStore.delete(0)).isFalse();
    }

    @Test
    public void whenUpdateTaskWithoutCreatedDoneIsSuccessfully() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", "Купить крепкий чай", Timestamp.valueOf(date1), true);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isEqualTo(Optional.ofNullable(updateIask));
    }

    @Test
    public void whenUpdateTaskWithOnlyDoneIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isEqualTo(Optional.ofNullable(taskSaved));
    }

    @Test
    public void whenUpdateTaskWithOnlyCreatedIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date2), true);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isEqualTo(Optional.ofNullable(taskSaved));
    }

    @Test
    public void whenSetDoneWhenNotDoneIsSuccessfully() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), false);
        Task taskSaved = taskStore.create(task);
        Assertions.assertThat(taskStore.setDone(taskSaved.getId())).isTrue();
    }

    @Test
    public void whenSetDoneWhenDoneIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Assertions.assertThat(taskStore.setDone(taskSaved.getId())).isFalse();
    }

    @Test
    public void whenUpdateTaskNotNameIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, null, "Купить крепкий чай", Timestamp.valueOf(date1), false);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isEqualTo(Optional.ofNullable(task));
    }

    @Test
    public void whenUpdateTaskNotDescriptionIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", null, Timestamp.valueOf(date1), false);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isEqualTo(Optional.ofNullable(task));
    }

    @Test
    public void whenUpdateTaskNotCreatedIsFail() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task taskSaved = taskStore.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", "Купить крепкий чай", null, false);
        taskStore.update(updateIask);
        Assertions.assertThat(taskStore.findById(id)).isNotEqualTo(Optional.ofNullable(updateIask));
    }

    @Test
    public void whenTestFindById() {
        Task task = new Task(5, "Кино", "Сходить в кино с друзьями", Timestamp.valueOf(date1), true);
        Task createdTask = taskStore.create(task);
        Optional<Task> result = taskStore.findById(createdTask.getId());
        Assertions.assertThat(result).isEqualTo(Optional.ofNullable(createdTask));
    }

    @Test
    public void whenTestFindByIdNotExist() {
        Optional<Task> result = taskStore.findById(0);
        Assertions.assertThat(result).isEqualTo(Optional.empty());
    }
}
package ru.job4j.repository;

import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import ru.job4j.model.Category;
import ru.job4j.model.Priority;
import ru.job4j.model.Task;
import ru.job4j.model.TodoUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.job4j.converter.ConverterDateTime.getLocalDateTimeFromString;

class TaskRepositoryTest {

    private static final String date1 = "07.08.2025 06:00:01";
    private static final String date2 = "07.09.2025 06:00:01";
    private static TaskRepository taskRepository;
    private static TodoUserRepository todoUserRepository;
    private static PriorityRepository priorityRepository;
    private static CategoryRepository categoryRepository;
    private static TodoUser userCreated;
    private static Priority priority;
    private static Category category1;
    private static Category category2;
    private static Set<Category> categories;

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

        taskRepository = new TaskRepository(new CrudRepository(sf));

        todoUserRepository = new TodoUserRepository(new CrudRepository(sf));
        var user = new TodoUser(null, "Ольга", "olga", "pass", null);
        userCreated = todoUserRepository.save(user);

        priorityRepository = new PriorityRepository(new CrudRepository(sf));
        var priorityNew = new Priority(null, "Срочный", 1);
        priority = priorityRepository.save(priorityNew);

        categoryRepository = new CategoryRepository(new CrudRepository(sf));
        var categoryNew = new Category(null, "Здоровье");
        category1 = categoryRepository.save(categoryNew);
        categoryNew = new Category(null, "Саморазвитие");
        category2 = categoryRepository.save(categoryNew);
        categories = Set.of(category1, category2);
    }

    @AfterAll
    public static void clearRepositories() {

        clearAllRepositories();
    }

    @AfterEach
    public void clearTasksAfter() {

        clearTasks();
    }

    @BeforeEach
    public void clearTasksBefore() {

        clearTasks();
    }

    private static void clearAllRepositories() {
        todoUserRepository.deleteByLogin(userCreated.getLogin());
        priorityRepository.deleteByName(priority.getName());
        categoryRepository.deleteByName(category1.getName());
        categoryRepository.deleteByName(category2.getName());
    }

    private void clearTasks() {
        var tasks = taskRepository.findAll();
        for (var task : tasks) {
            taskRepository.delete(task.getId().intValue());
        }
    }

    @Test
    public void whenCreateThenGetSame() {
        var task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        var taskCreated = taskRepository.create(task);
        var createdTask = taskRepository.findByName(task.getName());
        assertThat(createdTask).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(Optional.ofNullable(taskCreated));
    }

    @Test
    public void whenCreateSeveralThenGetAll() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task2 = taskRepository.create(new Task(null, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task3 = taskRepository.create(new Task(null, "Английский", "Выполнить задание по английскомй",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var result = taskRepository.findAll();
        assertThat(result).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(List.of(task1, task2, task3));
    }

    @Test
    public void whenCreateSeveralThenGetAllDone() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task2 = taskRepository.create(new Task(null, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var task3 = taskRepository.create(new Task(null, "Английский", "Выполнить задание по английскомй",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var result = taskRepository.findAllDone();
        assertThat(result).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(List.of(task1, task3));
    }

    @Test
    public void whenCreateSeveralThenGetAllNew() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var task2 = taskRepository.create(new Task(null, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task3 = taskRepository.create(new Task(null, "Английский", "Выполнить задание по английскомй",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var result = taskRepository.findAllNew();
        assertThat(result).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(List.of(task1, task3));
    }

    @Test
    public void whenTestFindByNameIsSuchTask() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var task2 = taskRepository.create(new Task(null, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task3 = taskRepository.create(new Task(null, "Английский", "Выполнить задание по английскомй",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var result = taskRepository.findByName(task2.getName());
        assertThat(result).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(Optional.ofNullable(task2));
    }

    @Test
    public void whenTestFindByNameIsNotSuchTask() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var task2 = taskRepository.create(new Task(null, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var task3 = taskRepository.create(new Task(null, "Английский", "Выполнить задание по английскомй",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories));
        var result = taskRepository.findByName("Отпуск");
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(taskRepository.findAll()).isEqualTo(emptyList());
        assertThat(taskRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var task1 = taskRepository.create(new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories));
        var isDeleted = taskRepository.delete(task1.getId().intValue());
        var createdTask = taskRepository.findById(task1.getId());
        assertThat(isDeleted).isTrue();
        assertThat(createdTask).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetTrue() {
        assertThat(taskRepository.delete(1)).isFalse();
    }

    @Test
    public void whenUpdateTaskWithoutCreatedDoneIsSuccessfully() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", "Купить крепкий чай",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        taskRepository.update(updateIask);
        assertThat(taskRepository.findById(id)).usingRecursiveComparison().comparingOnlyFields("name", "description", "done")
                .isEqualTo(Optional.ofNullable(updateIask));
    }

    @Test
    public void whenUpdateTaskWithOnlyDoneIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        Task updateIask = new Task(taskSaved.getId(), "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories);
        taskRepository.update(updateIask);
        Assertions.assertThat(taskRepository.findById(taskSaved.getId())).isEqualTo(Optional.ofNullable(taskSaved));
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().isDone()).isEqualTo(taskSaved.isDone());
    }

    @Test
    public void whenUpdateTaskWithOnlyCreatedIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        Task updateIask = new Task(taskSaved.getId(), "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date2), true, userCreated, priority, categories);
        taskRepository.update(updateIask);
        Assertions.assertThat(taskRepository.findById(taskSaved.getId())).isEqualTo(Optional.ofNullable(taskSaved));
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().getCreated()).isEqualTo(taskSaved.getCreated());
    }

    @Test
    public void whenSetDoneWhenNotDoneIsSuccessfully() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        Assertions.assertThat(taskRepository.setDone(taskSaved.getId())).isTrue();
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().isDone()).isTrue();
    }

    @Test
    public void whenSetDoneWhenDoneIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        Assertions.assertThat(taskRepository.setDone(taskSaved.getId())).isFalse();
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().isDone()).isTrue();
    }

    @Test
    public void whenUpdateTaskNotNameIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        Task updateIask = new Task(taskSaved.getId(), null, "Купить крепкий чай",
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories);
        try {
            taskRepository.update(updateIask);
        } catch (NullPointerException nullPointerException) {
            assertThrows(NullPointerException.class, () -> {
                throw nullPointerException;
            });
            Assertions.assertThat(taskRepository.findById(taskSaved.getId()))
                    .usingRecursiveComparison().ignoringFields("id", "created").isEqualTo(Optional.ofNullable(task));

        } catch (Exception exception) {
            fail("Expected NullPointerException, but caught: " + exception.getClass().getSimpleName());
        }
    }

    @Test
    public void whenUpdateTaskNotDescriptionIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", null,
                getLocalDateTimeFromString(date1), false, userCreated, priority, categories);
        try {
            taskRepository.update(updateIask);
        } catch (NullPointerException nullPointerException) {
            assertThrows(NullPointerException.class, () -> {
                throw nullPointerException;
            });
            Assertions.assertThat(taskRepository.findById(taskSaved.getId()))
                    .usingRecursiveComparison().ignoringFields("id", "created").isEqualTo(Optional.ofNullable(task));

        } catch (Exception exception) {
            fail("Expected NullPointerException, but caught: " + exception.getClass().getSimpleName());
        }

    }

    @Test
    public void whenUpdateTaskNotCreatedIsFail() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task taskSaved = taskRepository.create(task);
        int id = taskSaved.getId();
        Task updateIask = new Task(id, "Чай", "Купить крепкий чай", null, false, userCreated, priority, categories);
        taskRepository.update(updateIask);
        var t = taskRepository.findById(taskSaved.getId());
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().getName()).isEqualTo(updateIask.getName());
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().getDescription()).isEqualTo(updateIask.getDescription());
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().getCreated()).isEqualTo(task.getCreated());
        Assertions.assertThat(taskRepository.findById(taskSaved.getId()).get().isDone()).isEqualTo(task.isDone());
    }

    @Test
    public void whenTestFindById() {
        Task task = new Task(null, "Кино", "Сходить в кино с друзьями",
                getLocalDateTimeFromString(date1), true, userCreated, priority, categories);
        Task createdTask = taskRepository.create(task);
        Optional<Task> result = taskRepository.findById(createdTask.getId());
        Assertions.assertThat(result).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(Optional.ofNullable(createdTask));
    }

    @Test
    public void whenTestFindByIdNotExist() {
        Optional<Task> result = taskRepository.findById(0);
        Assertions.assertThat(result).isEqualTo(Optional.empty());
    }
}
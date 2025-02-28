package ru.job4j.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Task;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
/*@Table(name = "tasks", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})*/
@Repository
@AllArgsConstructor
public class TaskRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param task задание.
     * @return пользователь с id.
     */
    public Task create(Task task) {
        return crudRepository.runBoolean(session -> session.persist(task)) ? task : null;
    }

    /**
     * Обновить в базе задание.
     *
     * @param task задание.
     */
    public boolean update(Task task) {
        return crudRepository.queryBoolean(
                "UPDATE Task SET name = :fName, description = :fDescription, priority = :fPriority WHERE id = :fId",
                Map.of("fId", task.getId(), "fName", task.getName(),
                        "fDescription", task.getDescription(), "fPriority", task.getPriority()));
    }

    /**
     * Установить значение поля done в "true".
     *
     * @param id иденификатор задания.
     */
    public boolean setDone(int id) {
        return crudRepository.queryBoolean(
                "UPDATE Task SET done = true WHERE id = :fId AND done != true", Map.of("fId", id));
    }

    /**
     * Удалить задание по id.
     *
     * @param taskId ID
     */
    public boolean delete(int taskId) {
        return crudRepository.queryBoolean("DELETE Task WHERE id = :fId", Map.of("fId", taskId));
    }

    /**
     * Список заданий, отсортированных по id.
     *
     * @return список заданий.
     */
    public List<Task> findAll() {
        return crudRepository.query("from Task f JOIN FETCH f.priority order by f.id asc", Task.class);
    }

    /**
     * Список выполненных заданий.
     *
     * @return список заданий.
     */
    public List<Task> findAllDone() {
        return crudRepository.query("from Task f JOIN FETCH f.priority where f.done = true order by f.id asc", Task.class);
    }

    /**
     * Список новых заданий.
     *
     * @return список заданий.
     */
    public List<Task> findAllNew() {
        return crudRepository.query("from Task f JOIN FETCH f.priority where f.done = false order by f.id asc", Task.class);
    }

    /**
     * Найти задание по ID
     *
     * @return задание.
     */
    public Optional<Task> findById(int taskId) {
        return crudRepository.optional(
                "from Task f JOIN FETCH f.priority where f.id = :fId", Task.class, Map.of("fId", taskId)
        );
    }

    /**
     * Найти задания по name.
     *
     * @param name name.
     * @return Optional or task.
     */
    public Optional<Task> findByName(String name) {
        return crudRepository.optional(
                "from Task f JOIN FETCH f.priority where f.name = :fName", Task.class, Map.of("fName", name)
        );
    }
}

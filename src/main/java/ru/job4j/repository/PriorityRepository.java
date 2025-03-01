package ru.job4j.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Priority;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Repository
@AllArgsConstructor
public class PriorityRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param priority Приоритет.
     * @return приоритет с id.
     */
    public Priority save(Priority priority) {
        return crudRepository.runBoolean(session -> session.persist(priority)) ? priority : null;
    }

    /**
     * Удалить приоритет по name.
     *
     * @param name name
     */
    public boolean deleteByName(String name) {
        return crudRepository.queryBoolean(
                "DELETE Priority WHERE name = :fName", Map.of("fName", name)
        );
    }

    /**
     * Список приоритетов, отсортированных по id.
     *
     * @return список приоритетов.
     */
    public List<Priority> findAll() {
        return crudRepository.query("from Priority order by id asc", Priority.class);
    }

    /**
     * Найти приоритет по ID
     *
     * @return приоритет.
     */
    public Optional<Priority> findById(int id) {
        return crudRepository.optional(
                "from Priority where id = :fId", Priority.class, Map.of("fId", id)
        );
    }

    /**
     * Найти приоритет по name.
     *
     * @param name name.
     * @return Optional or prioritety.
     */
    public Optional<Priority> findByName(String name) {
        return crudRepository.optional(
                "from Priority where name = :fName", Priority.class, Map.of("fName", name)
        );
    }
}

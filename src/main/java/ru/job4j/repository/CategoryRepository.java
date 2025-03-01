package ru.job4j.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Category;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Repository
@AllArgsConstructor
public class CategoryRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param category Приоритет.
     * @return категория с id.
     */
    public Category save(Category category) {
        return crudRepository.runBoolean(session -> session.persist(category)) ? category : null;
    }

    /**
     * Удалить категория по name.
     *
     * @param name name
     */
    public boolean deleteByName(String name) {
        return crudRepository.queryBoolean(
                "DELETE Category WHERE name = :fName", Map.of("fName", name)
        );
    }

    /**
     * Список категорий, отсортированных по id.
     *
     * @return список категорий.
     */
    public List<Category> findAll() {
        return crudRepository.query("from Category order by id asc", Category.class);
    }

    /**
     * Найти категория по ID
     *
     * @return задание.
     */
    public Optional<Category> findById(int id) {
        return crudRepository.optional(
                "from Category where id = :fId", Category.class, Map.of("fId", id)
        );
    }

    /**
     * Найти категория по name.
     *
     * @param name name.
     * @return Optional or category.
     */
    public Optional<Category> findByName(String name) {
        return crudRepository.optional(
                "from Category where name = :fName", Category.class, Map.of("fName", name)
        );
    }
}
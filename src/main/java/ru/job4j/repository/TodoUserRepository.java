package ru.job4j.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.model.TodoUser;

import javax.persistence.Entity;
import java.util.Map;
import java.util.Optional;

@Entity
@Repository
@AllArgsConstructor
public class TodoUserRepository {
    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param user пользователь.
     * @return пользователь с id.
     */
    public TodoUser save(TodoUser user) {
        return crudRepository.runBoolean(session -> session.persist(user)) ? user : null;
    }

    /**
     * Удалить пользователя по id.
     *
     * @param userId ID
     */
    public boolean delete(int userId) {
        return crudRepository.queryBoolean(
                "DELETE TodoUser WHERE id = :fId", Map.of("fId", userId)
        );
    }

    /**
     * Удалить пользователя по login.
     *
     * @param login login
     */
    public boolean deleteByLogin(String login) {
        return crudRepository.queryBoolean(
                "DELETE TodoUser WHERE login = :fLogin", Map.of("fLogin", login)
        );
    }

    /**
     * Найти пользователя по login.
     *
     * @param login login.
     * @return Optional or user.
     */
    public Optional<TodoUser> findByLogin(String login, String password) {
        return crudRepository.optional(
                "from TodoUser where login = :fLogin AND password = :fPassword", TodoUser.class,
                Map.of("fLogin", login, "fPassword", password)
        );
    }

    /**
     * Найти пользователя по id.
     *
     * @param id id.
     * @return Optional or user.
     */
    public Optional<TodoUser> findById(String id) {
        return crudRepository.optional(
                "from TodoUser where id = :fId", TodoUser.class,
                Map.of("fId", id)
        );
    }
}
package ru.job4j.service.todouser;


import ru.job4j.model.TodoUser;

import java.util.Optional;

public interface TodoUserService {

    Optional<TodoUser> save(TodoUser todoUser);

    Optional<TodoUser> findByLogin(String login, String password);
}

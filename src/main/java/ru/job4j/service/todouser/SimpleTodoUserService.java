package ru.job4j.service.todouser;

import org.springframework.stereotype.Service;
import ru.job4j.model.TodoUser;
import ru.job4j.store.TodoUserStore;

import java.util.Optional;

@Service
public class SimpleTodoUserService implements TodoUserService {

    private final TodoUserStore todoUserStore;

    public SimpleTodoUserService(TodoUserStore todoUserStore) {

        this.todoUserStore = todoUserStore;
    }

    @Override
    public Optional<TodoUser> save(TodoUser todoUser) {

        return todoUserStore.save(todoUser);
    }

    @Override
    public Optional<TodoUser> findByLogin(String login, String password) {

        return todoUserStore.findByLogin(login, password);
    }
}

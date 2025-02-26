package ru.job4j.service.todouser;

import org.springframework.stereotype.Service;
import ru.job4j.model.TodoUser;
import ru.job4j.repository.TodoUserRepository;

import java.util.Optional;

@Service
public class SimpleTodoUserService implements TodoUserService {

    private final TodoUserRepository todoUserRepository;

    public SimpleTodoUserService(TodoUserRepository todoUserRepository) {

        this.todoUserRepository = todoUserRepository;
    }

    @Override
    public Optional<TodoUser> save(TodoUser todoUser) {

        return Optional.ofNullable(todoUserRepository.save(todoUser));
    }

    @Override
    public Optional<TodoUser> findByLogin(String login, String password) {

        return todoUserRepository.findByLogin(login, password);
    }
}

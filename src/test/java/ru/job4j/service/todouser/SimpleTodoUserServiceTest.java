package ru.job4j.service.todouser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.model.TodoUser;
import ru.job4j.repository.TodoUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class SimpleTodoUserServiceTest {

    @Test
    void whenSaveSuccessfullyWithMock() {
        var user = new TodoUser(1, "OlgaI", "olga", "olgaPass", null);
        TodoUserRepository userRepositoryMock = mock(TodoUserRepository.class);
        when(userRepositoryMock.save(user)).thenReturn(user);
        TodoUserService simpleUserService = new SimpleTodoUserService(userRepositoryMock);
        Optional<TodoUser> savedUser = simpleUserService.save(user);
        assertThat(savedUser.get()).isEqualTo(user);
    }

    @Test
    void whenSaveFailWithMock() {
        var user = new TodoUser(1, "OlgaI", "olga", "olgaPass", null);
        TodoUserRepository userRepositoryMock = mock(TodoUserRepository.class);
        when(userRepositoryMock.save(user)).thenReturn(null);
        TodoUserService simpleUserService = new SimpleTodoUserService(userRepositoryMock);
        Optional<TodoUser> savedUser = simpleUserService.save(user);
        assertThat(savedUser).isEmpty();
    }

    @Test
    void whenFindByLoginSuccessfullyWithMock() {
        var user = new TodoUser(1, "OlgaI", "olga", "olgaPass", null);

        TodoUserRepository userRepositoryMock = mock(TodoUserRepository.class);
        when(userRepositoryMock
                .findByLogin(user.getLogin(), user.getPassword())).thenReturn(Optional.of(user));

        TodoUserService simpleUserService = new SimpleTodoUserService(userRepositoryMock);
        Optional<TodoUser> foundByAttrUser =
                simpleUserService.findByLogin(user.getLogin(), user.getPassword());

        assertThat(foundByAttrUser.get()).isEqualTo(user);
    }

    @Test
    void whenFindByEmailAndPasswordFailWithMock() {
        var user = new TodoUser(1, "OlgaI", "olga", "olgaPass", null);

        TodoUserRepository userRepositoryMock = mock(TodoUserRepository.class);
        when(userRepositoryMock
                .findByLogin(user.getLogin(), user.getPassword())).thenReturn(Optional.empty());

        TodoUserService simpleUserService = new SimpleTodoUserService(userRepositoryMock);
        Optional<TodoUser> foundByAttrUser =
                simpleUserService.findByLogin(user.getLogin(), user.getPassword());

        assertThat(foundByAttrUser).isEmpty();
    }
}
package ru.job4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.model.TodoUser;
import ru.job4j.service.todouser.TodoUserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private TodoUserService userService;

    private TodoUserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(TodoUserService.class);
        userController = new TodoUserController(userService);
    }

    @Test
    public void whenRequestUserByIdPageThenGetPageWithUser() {
        var user = new TodoUser(1, "OlgaI", "test1", "pass1");
        when(userService.findByLogin(user.getLogin(), user.getPassword())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.getUserByLoginPass(model, user.getLogin(), user.getPassword());
        var actualUser = model.getAttribute("user");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenPostUserThenSameDataAndRedirectToStartPage() throws Exception {
        var user = new TodoUser(1, "OlgaI", "test1", "pass1");
        var userArgumentCaptor = ArgumentCaptor.forClass(TodoUser.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/");
        assertThat(actualUser).isEqualTo(user);
    }
}
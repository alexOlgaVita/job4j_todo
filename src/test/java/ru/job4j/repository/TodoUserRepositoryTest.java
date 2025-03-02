package ru.job4j.repository;

import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.model.Task;
import ru.job4j.model.TodoUser;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class TodoUserRepositoryTest {

    public static final List<Map<String, String>> LOGIN_PASS = List.of(Map.of("olga", "olgaPass"),
            Map.of("olga", "svetaPass"),
            Map.of("nik", "nikPass"),
            Map.of("julia", "juliaPass")
    );

    private static TodoUserRepository todoUserRepository;

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
        todoUserRepository = new TodoUserRepository(new CrudRepository(sf));
    }

    @BeforeEach
    public void beforeClearUsers() {
        clearUsers();
    }

    @AfterEach
    public void afterClearUsers() {
        clearUsers();
    }

    private void clearUsers() {
        for (var user : LOGIN_PASS) {
            todoUserRepository.deleteByLogin(user.keySet().toArray()[0].toString());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var login = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass = LOGIN_PASS.get(0).get(login);
        var user = todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login.toString(), pass, null));
        var savedUser = todoUserRepository.findByLogin(user.getLogin(), user.getPassword()).get();
        Assertions.assertThat(savedUser).usingRecursiveComparison().comparingOnlyFields("name", "login", "password")
                .isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralDiffloginThenGetNeed() {
        var login = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass = LOGIN_PASS.get(0).get(login);
        var user = todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login.toString(), pass, null));
        var savedUser = todoUserRepository.findByLogin(login.toString(), pass).get();
        var login2 = LOGIN_PASS.get(2).keySet().toArray()[0];
        var pass2 = LOGIN_PASS.get(2).get(login2);
        var user2 = todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login2.toString(), pass2, null));
        var savedUser2 = todoUserRepository.findByLogin(login2.toString(), pass2).get();
        var login3 = LOGIN_PASS.get(3).keySet().toArray()[0];
        var pass3 = LOGIN_PASS.get(3).get(login3);
        var user3 = todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login3.toString(), pass3, null));
        var savedUser3 = todoUserRepository.findByLogin(login3.toString(), pass3).get();
        Assertions.assertThat(savedUser).usingRecursiveComparison().comparingOnlyFields("name", "login", "password")
                .isEqualTo(user);
        Assertions.assertThat(savedUser2).usingRecursiveComparison().comparingOnlyFields("name", "login", "password")
                .isEqualTo(user2);
        Assertions.assertThat(savedUser3).usingRecursiveComparison().comparingOnlyFields("name", "login", "password")
                .isEqualTo(user3);
    }

    @Test
    public void whenSaveSameThenError() {
        var login1 = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass1 = LOGIN_PASS.get(0).get(login1);
        var user1 = todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login1.toString(), pass1, null));
        var savedUser1 = todoUserRepository.findByLogin(login1.toString(), pass1).get();
        Assertions.assertThat(savedUser1).usingRecursiveComparison().comparingOnlyFields("name", "login", "password")
                .isEqualTo(user1);
        var login2 = LOGIN_PASS.get(1).keySet().toArray()[0];
        var pass2 = LOGIN_PASS.get(1).get(login2);
        try {
            todoUserRepository.save(new TodoUser(null, "Ильина Ольга", login2.toString(), pass2, null));
        } catch (PersistenceException persistenceException) {
            assertThrows(PersistenceException.class, () -> {
                throw persistenceException;
            });
        } catch (Exception exception) {
            fail("Expected PersistenceException, but caught: " + exception.getClass().getSimpleName());
        }
    }
}
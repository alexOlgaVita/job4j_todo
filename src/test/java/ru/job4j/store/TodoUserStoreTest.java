package ru.job4j.store;

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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TodoUserStoreTest {

    public static final List<Map<String, String>> LOGIN_PASS = List.of(Map.of("olga", "olgaPass"),
            Map.of("olga", "svetaPass"),
            Map.of("nik", "nikPass"),
            Map.of("julia", "juliaPass")
    );

    private static TodoUserStore todoUserStore;

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
        todoUserStore = new TodoUserStore(sf);
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
            todoUserStore.deleteByLogin(user.keySet().toArray()[0].toString());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var login = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass = LOGIN_PASS.get(0).get(login);
        var user = todoUserStore.save(new TodoUser(3, "Ильина Ольга", login.toString(), pass));
        var savedUser = todoUserStore.findByLogin(user.get().getLogin(), user.get().getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user.get());
    }

    @Test
    public void whenSaveSeveralDiffloginThenGetNeed() {
        var login = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass = LOGIN_PASS.get(0).get(login);
        var user = todoUserStore.save(new TodoUser(1, "Ильина Ольга", login.toString(), pass));
        var savedUser = todoUserStore.findByLogin(user.get().getLogin(), user.get().getPassword()).get();
        var login2 = LOGIN_PASS.get(2).keySet().toArray()[0];
        var pass2 = LOGIN_PASS.get(2).get(login2);
        var user2 = todoUserStore.save(new TodoUser(2, "Ильина Ольга", login2.toString(), pass2));
        var savedUser2 = todoUserStore.findByLogin(user2.get().getLogin(), user2.get().getPassword()).get();
        var login3 = LOGIN_PASS.get(3).keySet().toArray()[0];
        var pass3 = LOGIN_PASS.get(3).get(login3);
        var user3 = todoUserStore.save(new TodoUser(3, "Ильина Ольга", login3.toString(), pass3));
        var savedUser3 = todoUserStore.findByLogin(user3.get().getLogin(), user3.get().getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user.get());
        assertThat(savedUser2).usingRecursiveComparison().isEqualTo(user2.get());
        assertThat(savedUser3).usingRecursiveComparison().isEqualTo(user3.get());
    }

    @Test
    public void whenSaveSameThenError() {
        var login1 = LOGIN_PASS.get(0).keySet().toArray()[0];
        var pass1 = LOGIN_PASS.get(0).get(login1);
        var user1 = todoUserStore.save(new TodoUser(0, "Ильина Ольга", login1.toString(), pass1));
        var savedUser1 = todoUserStore.findByLogin(user1.get().getLogin(), user1.get().getPassword()).get();
        assertThat(savedUser1).usingRecursiveComparison().isEqualTo(user1.get());
        var login2 = LOGIN_PASS.get(1).keySet().toArray()[0];
        var pass2 = LOGIN_PASS.get(1).get(login2);
        var user2 = todoUserStore.save(new TodoUser(0, "Ильина Ольга", login2.toString(), pass2));
        assertThat(savedUser1).usingRecursiveComparison().isEqualTo(user1.get());
        assertThat(user2).isEmpty();
    }
}
package ru.job4j.store;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.model.TodoUser;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "todo_users")
@Repository
@AllArgsConstructor
public class TodoUserStore {

    private final SessionFactory sf;

    public Optional<TodoUser> save(TodoUser todoUser) {
        Session session = sf.openSession();
        TodoUser result = null;
        if (todoUser.getName() != null && !todoUser.getName().isBlank()) {
            try {
                session.beginTransaction();
                session.save(todoUser);
                session.getTransaction().commit();
                result = todoUser;
            } catch (Exception e) {
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return Optional.ofNullable(result);
    }

    /**
     * Найти пользователя по login
     *
     * @return Optional or todoUser.
     */
    public Optional<TodoUser> findByLogin(String login, String password) {
        Session session = sf.openSession();
        Optional<TodoUser> result = Optional.empty();
        try {
            Query<TodoUser> query = session.createQuery(
                    "from TodoUser as t where t.login = :flogin AND t.password = :fpassword", TodoUser.class);
            query.setParameter("flogin", login);
            query.setParameter("fpassword", password);
            result = Optional.ofNullable(query.uniqueResult());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Удалить пользователю с заданным логином .
     *
     * @param login
     */
    public boolean deleteByLogin(String login) {
        Session session = sf.openSession();
        boolean result = false;
        try {
            session.beginTransaction();
            result = (session.createQuery(
                            "DELETE TodoUser WHERE login = :fId")
                    .setParameter("fId", login)
                    .executeUpdate() > 0);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }
}

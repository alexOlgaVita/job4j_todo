package ru.job4j.store;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Task;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tasks")
@Repository
@AllArgsConstructor
public class TaskStore {

    private final SessionFactory sf;

    /**
     * Сохранить в базе.
     *
     * @param task задание.
     * @return задание с id.
     */
    public Task create(Task task) {
        Session session = sf.openSession();
        Task result = null;
        if (task.getName() != null && !task.getName().isBlank()) {
            try {
                session.beginTransaction();
                session.save(task);
                session.getTransaction().commit();
                result = task;
            } catch (Exception e) {
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return result;
    }

    /**
     * Обновить в базе задание.
     *
     * @param task задание.
     */
    public boolean update(Task task) {
        Session session = sf.openSession();
        boolean result = false;
        Task taskOld = findById(task.getId()).get();
        if ((task.getName() != null && task.getDescription() != null)
                && (!task.getName().isBlank())
                && (!taskOld.getName().equals(task.getName()) || !taskOld.getDescription().equals(task.getDescription()))) {
            try {
                session.beginTransaction();
                result = (session.createQuery(
                                "UPDATE Task SET name = :fname, description = :fdescription "
                                        + "WHERE id = :fId")
                        .setParameter("fId", task.getId())
                        .setParameter("fname", task.getName())
                        .setParameter("fdescription", task.getDescription())
                        .executeUpdate() > 0);

                session.createQuery(
                                "UPDATE Task SET name = :fname, description = :fdescription "
                                        + "WHERE id = :fId")
                        .setParameter("fId", task.getId())
                        .setParameter("fname", task.getName())
                        .setParameter("fdescription", task.getDescription())
                        .executeUpdate();
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return result;
    }

    /**
     * Установить значение поля done в "true".
     *
     * @param id иденификатор задания.
     */
    public boolean setDone(int id) {
        Session session = sf.openSession();
        boolean result = false;
        if (!findById(id).get().isDone()) {
            try {
                session.beginTransaction();
                result = (session.createQuery(
                                "UPDATE Task SET done = true WHERE id = :fId")
                        .setParameter("fId", id)
                        .executeUpdate() > 0);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return result;
    }

    /**
     * Удалить задание по id.
     *
     * @param id ID
     */
    public boolean delete(int id) {
        Session session = sf.openSession();
        boolean result = false;
        try {
            session.beginTransaction();
            result = (session.createQuery(
                            "DELETE Task WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate() > 0);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Список всех заданий.
     *
     * @return список заданий.
     */
    public List<Task> findAll() {
        List<Task> result = List.of();
        Session session = sf.openSession();
        try {
            Query query = session.createQuery("from Task");
            result = query.list().stream().sorted(Comparator.comparing(Task::getId)).toList();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Список выполненных заданий.
     *
     * @return список заданий.
     */
    public List<Task> findAllDone() {
        return findAllByFilter(true).stream().sorted(Comparator.comparing(Task::getId)).toList();
    }

    /**
     * Список новых заданий.
     *
     * @return список заданий.
     */
    public List<Task> findAllNew() {
        return findAllByFilter(false).stream().sorted(Comparator.comparing(Task::getId)).toList();
    }

    /**
     * Список заданий, отфильтрованных по состоянию.
     *
     * @return список заданий.
     */
    private List<Task> findAllByFilter(boolean done) {
        List<Task> result = List.of();
        Session session = sf.openSession();
        try {
            Query query = session.createQuery("from Task as t where t.done = :fdone");
            query.setParameter("fdone", done);
            result = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Найти задание по ID
     *
     * @return Optional or task.
     */
    public Optional<Task> findById(int taskId) {
        Session session = sf.openSession();
        Optional<Task> result = Optional.empty();
        try {
            Query<Task> query = session.createQuery(
                    "from Task as t where t.id = :fId", Task.class);
            query.setParameter("fId", taskId);
            result = Optional.ofNullable(query.uniqueResult());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * Найти задание по name.
     *
     * @param name name.
     * @return Optional or task.
     */
    public Optional<Task> findByName(String name) {
        Session session = sf.openSession();
        Optional<Task> result = Optional.empty();
        try {
            Query<Task> query = session.createQuery(
                    "from Task as t where t.name = :fname", Task.class);
            query.setParameter("fname", name);
            result = Optional.ofNullable(query.uniqueResult());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
}
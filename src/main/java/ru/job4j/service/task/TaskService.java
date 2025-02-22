package ru.job4j.service.task;

import ru.job4j.dto.TaskDto;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {

    TaskDto create(TaskDto taskDto);

    boolean update(TaskDto task);

    boolean delete(int id);

    Collection<TaskDto> findAll();

    Collection<TaskDto> findAllDone();

    Collection<TaskDto> findAllNew();

    Optional<TaskDto> findById(int id);

    Optional<TaskDto> findByName(String name);
}
package ru.job4j.service.task;

import org.springframework.stereotype.Service;
import ru.job4j.dto.TaskDto;
import ru.job4j.mapper.TaskMapper;
import ru.job4j.model.Task;
import ru.job4j.store.TaskStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleTaskService implements TaskService {

    private final TaskStore taskRepository;
    private final TaskMapper taskMapper;

    public SimpleTaskService(TaskStore sql2oTaskRepository, TaskMapper taskMapper) {
        this.taskRepository = sql2oTaskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDto create(TaskDto taskDto) {
        Task task = taskMapper.getEntityFromModelCustom(taskDto);
        Task result = taskRepository.create(task);
        return taskMapper.getModelFromEntityCustom(result);
    }

    @Override
    public boolean update(TaskDto taskDto) {
        return taskRepository.update(taskMapper.getEntityFromModelCustom(taskDto));
    }

    @Override
    public boolean setDone(int id) {
        return taskRepository.setDone(findById(id).get().getId());
    }

    @Override
    public boolean delete(int id) {
        return taskRepository.delete(id);
    }

    @Override
    public Collection<TaskDto> findAll() {
        List<TaskDto> result = new ArrayList<>();
        if (taskRepository.findAll() != null) {
            result = taskRepository.findAll().stream()
                    .map(e -> taskMapper.getModelFromEntityCustom(e)).toList();
        }
        return result;
    }

    @Override
    public Collection<TaskDto> findAllDone() {
        List<TaskDto> result = new ArrayList<>();
        if (taskRepository.findAllDone() != null) {
            result = taskRepository.findAllDone().stream()
                    .map(e -> taskMapper.getModelFromEntityCustom(e)).toList();
        }
        return result;
    }

    @Override
    public Collection<TaskDto> findAllNew() {
        List<TaskDto> result = new ArrayList<>();
        if (taskRepository.findAllNew() != null) {
            result = taskRepository.findAllNew().stream()
                    .map(e -> taskMapper.getModelFromEntityCustom(e)).toList();
        }
        return result;
    }

    @Override
    public Optional<TaskDto> findById(int id) {
        return (taskRepository.findById(id).isPresent())
                ? Optional.ofNullable(taskMapper.getModelFromEntityCustom(taskRepository.findById(id).get())) : Optional.empty();
    }

    @Override
    public Optional<TaskDto> findByName(String name) {
        return (taskRepository.findByName(name).isPresent())
                ? Optional.ofNullable(taskMapper.getModelFromEntityCustom(taskRepository.findByName(name).get())) : Optional.empty();
    }
}
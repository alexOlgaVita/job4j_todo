package ru.job4j.mapper;

import org.mapstruct.Mapper;
import ru.job4j.dto.TaskDto;
import ru.job4j.model.Task;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    default TaskDto getModelFromEntityCustom(Task task) {
        TaskDto taskDto = new TaskDto();
        if (task != null) {
            taskDto.setId(task.getId());
            taskDto.setName(task.getName());
            taskDto.setDescription(task.getDescription());
            taskDto.setCreated(task.getCreated());
            taskDto.setCreateDate(new Date(task.getCreated().getTime()));
            taskDto.setDone(task.isDone());
        } else {
            taskDto = null;
        }
        return taskDto;
    }

    default Task getEntityFromModelCustom(TaskDto taskDto) {
        Task task = new Task();
        if (taskDto != null) {
            task.setId(taskDto.getId());
            task.setName(taskDto.getName());
            task.setDescription(taskDto.getDescription());
            task.setCreated(taskDto.getCreated());
            task.setDone(taskDto.isDone());
        } else {
            task = null;
        }
        return task;
    }
}

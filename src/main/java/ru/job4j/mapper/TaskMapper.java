package ru.job4j.mapper;

import org.mapstruct.Mapper;
import ru.job4j.dto.TaskDto;
import ru.job4j.model.Task;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static ru.job4j.converter.ConverterDateTime.getDate;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    default TaskDto getModelFromEntityCustom(Task task) {
        TaskDto taskDto = new TaskDto();
        if (task != null) {
            taskDto.setId(task.getId() == null ? 0 : task.getId());
            taskDto.setName(task.getName());
            taskDto.setDescription(task.getDescription());
            taskDto.setCreated(task.getCreated());
            taskDto.setCreateDate(getDate(task.getCreated()));
            taskDto.setDone(task.isDone());
            taskDto.setTodoUser(task.getTodoUser());
            taskDto.setPriority(task.getPriority());
        } else {
            taskDto = null;
        }
        return taskDto;
    }

    default Task getEntityFromModelCustom(TaskDto taskDto) {
        Task task = new Task();
        if (taskDto != null) {
            task.setId(taskDto.getId() == 0 ? null : taskDto.getId());
            task.setName(taskDto.getName());
            task.setDescription(taskDto.getDescription());
            task.setCreated(taskDto.getCreated());
            task.setDone(taskDto.isDone());
            task.setTodoUser(taskDto.getTodoUser());
            task.setPriority(taskDto.getPriority());
        } else {
            task = null;
        }
        return task;
    }

    private Date toDate(LocalDateTime localDateTime) {
        var instant = Timestamp.valueOf(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toInstant();
        var date = Date.from(instant);
        System.out.println(date);
        return date;
    }
}

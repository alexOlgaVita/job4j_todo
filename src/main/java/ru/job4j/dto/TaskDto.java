package ru.job4j.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.model.Priority;
import ru.job4j.model.TodoUser;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.LocalDateTime.now;

@Data
@NoArgsConstructor
public class TaskDto {

    private int id;
    private String name;
    private String description;
    private boolean done = false;
    private LocalDateTime created = now();
    private Date createDate = new Date(System.currentTimeMillis());
    private TodoUser todoUser;
    private Priority priority;

    public TaskDto(int id, String name, String description, LocalDateTime created, Date createDate,
                   boolean done, TodoUser todoUser, Priority priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.createDate = createDate;
        this.done = done;
        this.todoUser = todoUser;
        this.priority = priority;
    }
}

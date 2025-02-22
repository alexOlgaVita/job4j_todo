package ru.job4j.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
public class TaskDto {

    private int id;
    private String name;
    private String description;
    private boolean done = false;
    private Timestamp created = new Timestamp(System.currentTimeMillis());
    private Date createDate = new Date(System.currentTimeMillis());

    public TaskDto(int id, String name, String description, Timestamp created, Date createDate, boolean done) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.createDate = createDate;
        this.done = done;
    }
}

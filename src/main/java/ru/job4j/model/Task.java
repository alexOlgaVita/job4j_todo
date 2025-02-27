package ru.job4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.LocalDateTime.now;
import static ru.job4j.converter.ConverterDateTime.FORMATTER;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;
    private String name;
    private String description;
    private LocalDateTime created = now();
    private boolean done = false;

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", created=" + created != null ? created.format(FORMATTER) : "null" + '\''
                + ", done=" + ((done) ? "выполнено" : "не выполнено")
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return (id == task.id);
    }
}

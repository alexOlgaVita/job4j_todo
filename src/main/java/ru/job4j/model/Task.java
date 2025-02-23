package ru.job4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private Timestamp created = new Timestamp(System.currentTimeMillis());
    private boolean done = false;

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", created=" + new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").format(created) + '\''
                + ", done=" + ((done) ? "выполнено" : "не выполнено")
                + '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + id;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        return result;
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
        return (id == task.id) && (name.equals(task.name)
                && (description.equals(task.description)) && isSameTimeStamp(created, task.created));
    }

    private boolean isSameTimeStamp(Timestamp timestamp1,
                                    Timestamp timestamp2) {
        boolean result = false;
        if (timestamp1 != null && timestamp2 != null) {
            result = timestamp1.compareTo(timestamp2) == 0;
        } else if (timestamp1 == null && timestamp2 == null) {
            result = true;
        }
        return result;
    }
}

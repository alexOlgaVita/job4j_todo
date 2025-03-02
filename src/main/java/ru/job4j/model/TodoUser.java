package ru.job4j.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.TimeZone;

@Entity
@Table(name = "todo_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"login"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private String login;
    private String password;
    @Column(name = "user_zone")
    private String timezone = TimeZone.getDefault().getDisplayName();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoUser todoUser = (TodoUser) o;
        return login.equals(todoUser.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}

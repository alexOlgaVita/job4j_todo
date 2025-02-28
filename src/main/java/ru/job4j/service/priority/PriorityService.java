package ru.job4j.service.priority;

import ru.job4j.model.Priority;

import java.util.Collection;
import java.util.Optional;

public interface PriorityService {

    Collection<Priority> findAll();

    Optional<Priority> findById(int id);

    Optional<Priority> findByName(String name);
}
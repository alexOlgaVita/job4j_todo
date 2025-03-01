package ru.job4j.service.category;

import ru.job4j.model.Category;

import java.util.Collection;
import java.util.Optional;

public interface CategoryService {

    Collection<Category> findAll();

    Optional<Category> findById(int id);

    Optional<Category> findByName(String name);
}
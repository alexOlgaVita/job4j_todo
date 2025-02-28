package ru.job4j.service.priority;

import org.springframework.stereotype.Service;
import ru.job4j.model.Priority;
import ru.job4j.repository.PriorityRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimplePriorityService implements PriorityService {

    private final PriorityRepository priorityRepository;

    public SimplePriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @Override
    public Collection<Priority> findAll() {
        return priorityRepository.findAll();
    }

    @Override
    public Optional<Priority> findById(int id) {
        return priorityRepository.findById(id);
    }

    @Override
    public Optional<Priority> findByName(String name) {
        return priorityRepository.findByName(name);
    }
}
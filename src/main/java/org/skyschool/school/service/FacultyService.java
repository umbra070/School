package org.skyschool.school.service;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.repos.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    @Autowired
    FacultyRepository repository;

    @Transactional(readOnly = true)
    public HashSet<Faculty> getAll(){
        return new HashSet<>(repository.findAll());
    }

    public Faculty addFaculty(Faculty faculty) {
        return repository.save(faculty);
    }

    @Transactional(readOnly = true)
    public Faculty findFaculty(long id) {
        return repository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        return repository.save(faculty);
    }

    public void deleteFaculty(long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<Faculty> findFacultyByColor(String color) {
        return repository.findAll().stream()
                .filter(f -> Objects.equals(color, f.getColor()))
                .collect(Collectors.toSet());
    }
}

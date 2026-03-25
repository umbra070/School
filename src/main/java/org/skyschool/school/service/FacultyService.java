package org.skyschool.school.service;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.repos.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    @Autowired
    FacultyRepository repository;

//    public FacultyService(FacultyRepository repository){
//        this.repository = repository;
//    }

    public HashSet<Faculty> getAll(){
        return new HashSet<>(repository.findAll());
    }

    public Faculty addFaculty(Faculty faculty) {
        return repository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return repository.findById(id).get();
    }

    public Faculty editFaculty(Faculty faculty) {
        return repository.save(faculty);
    }

    public void deleteFaculty(long id) {
        repository.deleteById(id);
    }

    public Set<Faculty> findFacultyByColor(String color) {
        return repository.findAll().stream()
                .filter(f -> Objects.equals(color, f.getColor()))
                .collect(Collectors.toSet());
    }
}

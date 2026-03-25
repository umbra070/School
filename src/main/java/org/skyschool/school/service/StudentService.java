package org.skyschool.school.service;

import org.skyschool.school.model.Student;
import org.skyschool.school.repos.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentsRepository repository;

//    public StudentService(StudentsRepository repository){
//        this.repository = repository;
//    }

    public Student addStudent(Student student) {
        return repository.save(student);
    }

    public Student editStudent(Student student) {
        Student findStudent = repository.findById(student.getId()).get();
        return repository.save(student);
    }

    public Student findStudent(long id) {
        return repository.findById(id).get();
    }

    public HashSet<Student> getStudents() {
        return new HashSet<>(repository.findAll());
    }

    public void removeStudent(long id) {
        repository.deleteById(id);
    }

    public Set<Student> findStudentByAge(int age) {
        return repository.findAll().stream()
                .filter(s -> age == s.getAge())
                .collect(Collectors.toSet());
    }
}

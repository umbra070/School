package org.skyschool.school.service;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentsRepository repository;

    public Student addStudent(Student student) {
        return repository.save(student);
    }

    public Student editStudent(Student student) {
        if(repository.existsById(student.getId())){
            return repository.save(student);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Student findStudent(long id) {
        return repository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public HashSet<Student> getStudents() {
        return new HashSet<>(repository.findAll());
    }

    public void removeStudent(long id) {
        if(repository.existsById(id)){
            repository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentByAge(int age) {
        return repository.findAll().stream()
                .filter(s -> age == s.getAge())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsByRange(int min, int max){
        return repository.findStudentsByAgeRange(min, max);
    }

}

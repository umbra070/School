package org.skyschool.school.service;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.FacultyRepository;
import org.skyschool.school.repos.StudentsRepository;
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
    FacultyRepository fRepository;
    @Autowired
    StudentsRepository sRepository;

    @Transactional(readOnly = true)
    public HashSet<Faculty> getAll() {
        return new HashSet<>(fRepository.findAll());
    }

    @Transactional
    public Faculty addFaculty(Faculty faculty) {
        return fRepository.save(faculty);
    }

    @Transactional(readOnly = true)
    public Faculty findFaculty(long id) {
        return fRepository.findById(id).orElse(null);
    }

    @Transactional
    public Faculty editFaculty(Faculty faculty) {
        return fRepository.save(faculty);
    }

    @Transactional
    public boolean deleteFaculty(long id) {
        Faculty dbFaculty = fRepository.findById(id).orElse(null);
        if (dbFaculty == null) {
            return false;
        }
        dbFaculty.getStudents().stream()
                .peek(s -> s.setFaculty(null))
                .map(sRepository::save)
                .collect(Collectors.toSet());
        fRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public Set<Faculty> findFacultyByColor(String color) {
        return fRepository.findAll().stream()
                .filter(f -> Objects.equals(color, f.getColor()))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsInFaculty(Long facultyId) {
        if (!fRepository.existsById(facultyId)) {
            return null;
        }
        return new HashSet<>(fRepository.getStudents(facultyId));
    }

    @Transactional(readOnly = true)
    public boolean checkEntities(Long studentId, Long facultyId) {
        Student dbStudent = sRepository.findById(studentId).orElse(null);
        Faculty dbFaculty = fRepository.findById(facultyId).orElse(null);
        return dbStudent != null && dbFaculty != null;
    }

    @Transactional
    public boolean addRelationship(Long studentId, Long facultyId) {
        Student dbStudent = sRepository.findById(studentId).orElse(null);
        Faculty dbFaculty = fRepository.findById(facultyId).orElse(null);
        if (dbStudent == null || dbFaculty == null) {
            return false;
        }
        dbFaculty.addStudent(dbStudent);
        dbStudent.setFaculty(dbFaculty);
        fRepository.save(dbFaculty);
        sRepository.save(dbStudent);
        return true;
    }
}

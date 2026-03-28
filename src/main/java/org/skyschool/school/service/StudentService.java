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
public class StudentService {
    @Autowired
    private StudentsRepository sRepository;
    @Autowired
    private FacultyRepository fRepository;

    @Transactional
    public Student addStudent(Student student) {
        return sRepository.save(student);
    }

    @Transactional
    public Student editStudent(Student student) {
        Student dbStudent = sRepository.findById(student.getId()).orElse(null);
        if (dbStudent == null) {
            return null;
        }
        dbStudent.setAge(student.getAge());
        dbStudent.setName(student.getName());
        sRepository.save(dbStudent);
        return dbStudent;
    }

    @Transactional(readOnly = true)
    public Student findStudent(long id) {
        return sRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public HashSet<Student> getStudents() {
        return new HashSet<>(sRepository.findAll());
    }

    @Transactional
    public boolean removeStudent(Long id) {
        Student dbStudent = sRepository.findById(id).orElse(null);
        if (dbStudent == null) {
            return false;
        }
        if (dbStudent.isFacultyPresent()) {
            dbStudent.getFaculty().removeStudent(dbStudent);
            fRepository.save(dbStudent.getFaculty());
        }
        sRepository.deleteById(id);
        return true;
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

    @Transactional
    public boolean removeRelationship(Long studentId) {
        Student dbStudent = sRepository.findById(studentId).orElse(null);
        if (dbStudent == null) {
            return false;
        }
        Faculty faculty;
        if (!dbStudent.isFacultyPresent()) {
            return false;
        }
        faculty = dbStudent.getFaculty();
        faculty.removeStudent(dbStudent);
        dbStudent.setFaculty(null);
        fRepository.save(faculty);
        sRepository.save(dbStudent);
        return true;
    }

    @Transactional(readOnly = true)
    public Faculty getFacultyFromStudent(Long id) {
        Student dbStudent = sRepository.findById(id).orElse(null);
        if (dbStudent == null) {
            return null;
        }
        if (!dbStudent.isFacultyPresent()) {
            return null;
        }
        return dbStudent.getFaculty();
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentByAge(int age) {
        return sRepository.findStudentsByAge(age);
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsByRange(int min, int max) {
        return sRepository.findStudentsByAgeRange(min, max);
    }

}

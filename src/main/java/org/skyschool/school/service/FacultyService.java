package org.skyschool.school.service;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.FacultyRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository fRepository;
    @Autowired
    private StudentsRepository sRepository;

    Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Transactional(readOnly = true)
    public HashSet<Faculty> getAll() {
        HashSet<Faculty> foundFaculties = new HashSet<>(fRepository.findAll());
        logger.info("Service: FacultyService || Method: getAll || Input data(void): - " +
                "|| Output data(HashSet<Faculty>): [count: {}]", foundFaculties.size());
        return foundFaculties;
    }

    @Transactional
    public Faculty addFaculty(Faculty faculty) {
        Faculty foundFaculty = fRepository.save(faculty);
        logger.info("Service: FacultyService || Method: addFaculty || Input data(Faculty faculty): [{}] " +
                "|| Output data(Faculty faculty): [{}]", faculty, foundFaculty);
        return foundFaculty;
    }

    @Transactional(readOnly = true)
    public Faculty findFaculty(long id) {
        Faculty foundFaculty = fRepository.findById(id).orElse(null);
        logger.info("Service: FacultyService || Method: findFaculty || Input data(Long id): {} " +
                "|| Output data(Faculty foundFaculty): [{}]", id, foundFaculty);
        return foundFaculty;
    }

    @Transactional
    public Faculty editFaculty(Faculty faculty) {
        Faculty editedFaculty = fRepository.save(faculty);
        logger.info("Service: FacultyService || Method: editFaculty || Input data(Faculty faculty): [{}] " +
                "|| Output data(Faculty editedFaculty): [{}]", faculty, editedFaculty);
        return editedFaculty;
    }

    @Transactional
    public boolean deleteFaculty(long facultyId) {
        Faculty foundFaculty = fRepository.findById(facultyId).orElse(null);
        if (foundFaculty == null) {
            logger.warn("Service: FacultyService || Method: deleteFaculty || Input data(long facultyId): {} " +
                    "|| Output data(boolean): false " +
                    "|| Description: No such faculty", facultyId);
            return false;
        }
        foundFaculty.getStudents().stream()
                .peek(s -> s.setFaculty(null))
                .map(sRepository::save)
                .collect(Collectors.toSet());
        fRepository.deleteById(facultyId);
        logger.info("Service: FacultyService || Method: deleteFaculty || Input data(long facultyId): {} " +
                "|| Output data(boolean): true", facultyId);
        return true;
    }

    @Transactional(readOnly = true)
    public Set<Faculty> findFacultyByColor(String color) {
        Set<Faculty> foundFaculty = fRepository.findAll().stream()
                .filter(f -> Objects.equals(color, f.getColor()))
                .collect(Collectors.toSet());
        logger.info("Service: FacultyService || Method: findFacultyByColor || Input data(String color): {} " +
                "|| Output data(Set<Faculty>): count: {}", color, foundFaculty.size());
        return foundFaculty;
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsInFaculty(Long facultyId) {
        if (!fRepository.existsById(facultyId)) {
            logger.warn("Service: FacultyService || Method: findStudentsInFaculty || Input data(Long facultyId): {} " +
                    "|| Output data(Set<Student>): null " +
                    "|| Description: No such faculty", facultyId);
            return null;
        }
        Set<Student> foundStudents = new HashSet<>(fRepository.getStudents(facultyId));
        logger.info("Service: FacultyService || Method: findStudentsInFaculty || Input data(Long facultyId): {} " +
                "|| Output data(Set<Student>): count: {} ", facultyId, foundStudents.size());
        return foundStudents;
    }

    @Transactional(readOnly = true)
    public boolean checkEntities(Long studentId, Long facultyId) {
        Student foundStudent = sRepository.findById(studentId).orElse(null);
        Faculty foundFaculty = fRepository.findById(facultyId).orElse(null);
        return foundStudent != null && foundFaculty != null;
    }

    @Transactional
    public boolean addRelationship(Long studentId, Long facultyId) {
        Student foundStudent = sRepository.findById(studentId).orElse(null);
        Faculty foundFaculty = fRepository.findById(facultyId).orElse(null);
        if (foundStudent == null || foundFaculty == null) {
            logger.warn("Service: FacultyService || Method: addRelationship " +
                    "|| Input data(Long studentId, Long facultyId): [{} , {}] || Output data(boolean): false " +
                    "|| Description: No such student or faculty", studentId, facultyId);
            return false;
        }
        foundFaculty.addStudent(foundStudent);
        foundStudent.setFaculty(foundFaculty);
        fRepository.save(foundFaculty);
        sRepository.save(foundStudent);
        logger.info("Service: FacultyService || Method: addRelationship " +
                "|| Input data(Long studentId, Long facultyId): [{} , {}] " +
                "|| Output data(boolean): true", studentId, facultyId);
        return true;
    }

    @Transactional(readOnly = true)
    public String getLongestFacultyName(){
        List<Faculty> foundFaculties = fRepository.findAll();
        return foundFaculties.parallelStream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
}

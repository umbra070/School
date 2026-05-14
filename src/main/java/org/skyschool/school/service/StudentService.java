package org.skyschool.school.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.FacultyRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Transactional
    public Student addStudent(Student student) {
        if(student == null || student.getName().isEmpty()){
            logger.warn("Service: StudentService || Method: addStudent || Input data(Student student): [{}] || Output data(Student student): null || Description: Incorrect input data(Empty Student entity or empty student's name", student);
            return null;
        }
        logger.info("Service: StudentService || Method: addStudent || Input data(Student student): [{}] || Output data(Student student): [{}]", student, student);
        return sRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Integer getCount(){
        Integer count = sRepository.getStudentsCount();
        logger.info("Service: StudentService || Method: getCount || Input data(void): - || Output data(Integer): {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public Set<Student> getLastStudents(){
        Set<Student> lastStudents = sRepository.getFiveLastStudents();
        logger.info("Service: StudentService || Method: getLastStudents || Input data(void): - || Output data(Set<Student> lastStudents): count: {}", lastStudents.size());
        return lastStudents;
    }

    @Transactional(readOnly = true)
    public Integer getAverageStudentAge(){
        Integer averageAge = sRepository.getAverageAge();
        logger.info("Service: StudentService || Method: getAverageStudentAge || Input data(void): - || Output data(Integer averageAge): {}", averageAge);
        return averageAge;
    }

    @Transactional
    public Student editStudent(Student student) {
        if(student == null || student.getName().isEmpty() || student.getId() == null){
            logger.warn("Service: StudentService || Method: editStudent || Input data(Student student): [{}] " +
                    "|| Output data(Student foundStudent): null " +
                    "|| Description: Incorrect input data - Student entity, or name, or ID cannot be empty", student);
            return null;
        }
        Student foundStudent = sRepository.findById(student.getId()).orElse(null);
        if (foundStudent == null) {
            logger.warn("Service: StudentService || Method: editStudent || Input data(Student student): [{}] " +
                    "|| Output data(Student foundStudent): null " +
                    "|| Description: no such student in DB with this ID: {}", student, student.getId());
            return null;
        }
        foundStudent.setAge(student.getAge());
        foundStudent.setName(student.getName());
        sRepository.save(foundStudent);
        logger.info("Service: StudentService || Method: editStudent || Input data(Student student): [{}] " +
                "|| Output data(Student foundStudent): [{}] ", student, foundStudent);
        return foundStudent;
    }

    @Transactional(readOnly = true)
    public Student findStudent(long id) {
        Student foundStudent = sRepository.findById(id).orElse(null);
        if(foundStudent == null){
            logger.warn("Service: StudentService || Method: findStudent || Input data(Long id): {} " +
                    "|| Output data(Student foundStudent): null " +
                    "|| Description: no such student in DB", id);
            return null;
        }
        logger.info("Service: StudentService || Method: findStudent || Input data(Long id): {} " +
                "|| Output data(Student foundStudent): [{}]", id, foundStudent);
        return foundStudent;
    }

    @Transactional(readOnly = true)
    public HashSet<Student> getStudents() {
        HashSet<Student> students = new HashSet<>(sRepository.findAll());
        logger.info("Service: StudentService || Method: getStudents || Input data(void): - " +
                "|| Output data(HashSet<Student> students): {}", students.size());
        return students;
    }

    @Transactional
    public boolean removeStudent(Long id) {
        Student foundStudent = sRepository.findById(id).orElse(null);
        if (foundStudent == null) {
            logger.warn("Service: StudentService || Method: removeStudent || Input data(Long id): {} " +
                    "|| Output data(boolean): false || Description: no such Student entity", id);
            return false;
        }
        if (foundStudent.isFacultyPresent()) {
            foundStudent.getFaculty().removeStudent(foundStudent);
            fRepository.save(foundStudent.getFaculty());
        }
        sRepository.deleteById(id);
        logger.info("Service: StudentService || Method: removeStudent || Input data(Long id): {} " +
                "|| Output data(boolean): true",id);
        return true;
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
            logger.warn("Service: StudentService || Method: addRelationship " +
                    "|| Input data(Long studentId, Long facultyId): [{}, {}] || Output data(boolean): false " +
                    "|| Description: no such student or faculty", studentId, facultyId);
            return false;
        }
        foundFaculty.addStudent(foundStudent);
        foundStudent.setFaculty(foundFaculty);
        fRepository.save(foundFaculty);
        sRepository.save(foundStudent);
        logger.info("Service: StudentService || Method: addRelationship " +
                "|| Input data(Long studentId, Long facultyId): [{}, {}] " +
                "|| Output data(boolean): true", studentId, facultyId);
        return true;
    }

    @Transactional
    public boolean removeRelationship(Long studentId) {
        Student foundStudent = sRepository.findById(studentId).orElse(null);
        if (foundStudent == null) {
            logger.warn("Service: StudentService || Method: removeRelationship " +
                    "|| Input data(Long studentId): {} || Output data(boolean): false " +
                    "|| Description: no such student", studentId);
            return false;
        }
        Faculty faculty;
        if (!foundStudent.isFacultyPresent()) {
            logger.warn("Service: StudentService || Method: removeRelationship " +
                    "|| Input data(Long studentId): {} || Output data(boolean): false " +
                    "|| Description: no such faculty associated", studentId);
            return false;
        }
        faculty = foundStudent.getFaculty();
        faculty.removeStudent(foundStudent);
        foundStudent.setFaculty(null);
        fRepository.save(faculty);
        sRepository.save(foundStudent);
        logger.info("Service: StudentService || Method: removeRelationship " +
                "|| Input data(Long studentId): {} || Output data(boolean): true", studentId);
        return true;
    }

    @Transactional(readOnly = true)
    public Faculty getFacultyFromStudent(Long studentId) {
        Student foundStudent = sRepository.findById(studentId).orElse(null);
        if (foundStudent == null) {
            logger.info("Service: StudentService || Method: getFacultyFromStudent || Input data(Long studentId): {} " +
                    "|| Output data(Faculty foundStudent.getFaculty()): null " +
                    "|| Description: no such Student", studentId);
            return null;
        }
        if (!foundStudent.isFacultyPresent()) {
            logger.info("Service: StudentService || Method: getFacultyFromStudent || Input data(Long studentId): {} " +
                    "|| Output data(Faculty foundStudent.getFaculty()): null " +
                    "|| Description: no such Faculty associated with this student", studentId);
            return null;
        }
        logger.info("Service: StudentService || Method: getFacultyFromStudent || Input data(Long studentId): {} " +
                "|| Output data(Faculty foundStudent.getFaculty()): [{}]", studentId, foundStudent.getFaculty());
        return foundStudent.getFaculty();
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentByAge(int age) {
        Set<Student> foundStudents = sRepository.findStudentsByAge(age);
        logger.info("Service: StudentService || Method: findStudentByAge || Input data(int age): {} " +
                "|| Output data(Set<Student> foundStudents): count = {}", age, foundStudents.size());
        return foundStudents;
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsByRange(int min, int max) {
        Set<Student> foundStudents = sRepository.findStudentsByAgeRange(min, max);
        logger.info("Service: StudentService || Method: findStudentsByRange || Input data(int min, int max): [{}, {}] " +
                "|| Output data(Set<Student> foundStudents): count = {}", min, max, foundStudents.size());
        return foundStudents;
    }

}

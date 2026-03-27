package org.skyschool.school.service;

import org.springframework.transaction.annotation.Transactional;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.FacultyRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RelationshipService {
    @Autowired
    private FacultyRepository fRepository;
    @Autowired
    private StudentsRepository sRepository;

    @Transactional(readOnly=true)
    public Faculty getFacultyFromStudent(long studentId){
        return sRepository.getStudentFaculty(studentId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Set<Student> findStudentsInFaculty(long id){
        return new HashSet<>(fRepository.getStudents(id));
    }

    @Transactional
    public Student editStudent(Student student) {
        if(!sRepository.existsById(student.getId())){
            return null;
        }
        if(!(student.getFaculty() == null)){
            Faculty f = student.getFaculty();
            f.addStudent(student);
            student.setFaculty(f);
            fRepository.save(f);
        }
        return sRepository.save(student);
    }

    @Transactional(readOnly = true)
    public boolean checkEntities(long studentId, long facultyId){
        return sRepository.existsById(studentId) && fRepository.existsById(facultyId);
    }

    @Transactional
    public boolean addRelationship(long studentId, long facultyId){
        Student findStudent = sRepository.findById(studentId).orElse(null);
        Faculty findFaculty = fRepository.findById(facultyId).orElse(null);
        if (findFaculty != null && findStudent != null) {
            removeRelationship(studentId, facultyId);
            findStudent.setFaculty(findFaculty);
            findFaculty.addStudent(findStudent);
            fRepository.save(findFaculty);
            sRepository.save(findStudent);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean removeRelationship(long studentId, long facultyId) {
        Student findStudent = sRepository.findById(studentId).orElse(null);
        Faculty findFaculty = fRepository.findById(facultyId).orElse(null);
        if (findFaculty != null && findStudent != null) {
            findStudent.getFaculty().removeStudent(findStudent);
            findStudent.setFaculty(null);
            fRepository.save(findFaculty);
            sRepository.save(findStudent);
            return true;
        }
        return false;
    }
}

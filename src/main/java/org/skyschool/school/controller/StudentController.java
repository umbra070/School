package org.skyschool.school.controller;

import org.skyschool.school.model.Student;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    //GET http://localhost:8080/student/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id) {
        Student foundStudent = studentService.findStudent(id);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    //GET http://localhost:8080/student
    @GetMapping
    public ResponseEntity<HashSet<Student>> getStudents() {
        Set<Student> students = new HashSet<>();
        return ResponseEntity.ok(studentService.getStudents());
    }

    //GET http://localhost:8080/student/age/{age}
    @GetMapping("/age/{age}")
    public ResponseEntity<Set<Student>> getStudentsByAge(@PathVariable int age) {
        if (age <= 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(studentService.findStudentByAge(age));
    }

    //PUT http://localhost:8080/student
    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.findStudent(student.getId());
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(studentService.editStudent(student));
    }

    //POST http://localhost:8080/student
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    //DELETE http://localhost:8080/student/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        Student findStudent = studentService.findStudent(id);
        studentService.removeStudent(id);
        return ResponseEntity.ok(findStudent);
    }
}

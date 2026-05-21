package org.skyschool.school.controller;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.service.AvatarService;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService sService;


    //GET http://localhost:8080/student/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id) {
        Student foundStudent = sService.findStudent(id);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    //GET http://localhost:8080/student
    @GetMapping
    public ResponseEntity<HashSet<Student>> getStudents() {
        return ResponseEntity.ok(sService.getStudents());
    }

    //GET http://localhost:8080/student/{studentId}/faculty
    @GetMapping("/{studentId}/faculty")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long studentId) {
        Faculty findFaculty = sService.getFacultyFromStudent(studentId);
        if (findFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(findFaculty);
    }

    //GET http://localhost:8080/student/age/{age}
    @GetMapping("/age/{age}")
    public ResponseEntity<Set<Student>> getStudentsByAge(@PathVariable int age) {
        if (age < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(sService.findStudentByAge(age));
    }

    //PUT http://localhost:8080/student
    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = sService.findStudent(student.getId());
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(sService.editStudent(student));
    }

    //PUT http://localhost:8080/student/{studentId}/faculty/{facultyId}
    @PutMapping("{studentId}/faculty/{facultyId}")
    public ResponseEntity<Student> addFacultyToStudent(
            @PathVariable long studentId,
            @PathVariable long facultyId) {

        if (studentId < 0 || facultyId < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!sService.checkEntities(studentId, facultyId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(sService.addRelationship(studentId, facultyId)){
            return ResponseEntity.ok(sService.findStudent(studentId));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //POST http://localhost:8080/student
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return sService.addStudent(student);
    }

    //DELETE http://localhost:8080/student/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        boolean checkRemove = sService.removeStudent(id);
        if(!checkRemove){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.noContent().build();
    }

    //GET http://localhost:8080/student/age/range
    @GetMapping("/age/range")
    public ResponseEntity<Set<Student>> getStudentsByRange(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(sService.findStudentsByRange(min, max));
    }

    //GET http://localhost:8080/student/last/5
    @GetMapping("/last/5")
    public ResponseEntity<Set<Student>> getLastFiveStudents(){
        return ResponseEntity.ok(sService.getLastStudents());
    }

    //GET http://localhost:8080/student/age/average
    @GetMapping("/age/average")
    public ResponseEntity<Integer> getAverageAge(){
        //return ResponseEntity.ok(sService.getAverageStudentAge());
        return ResponseEntity.ok(sService.getAverageAge());
    }

    //GET http://localhost:8080/student/count
    @GetMapping("/count")
    public ResponseEntity<Integer> getStudentsCount(){
        return ResponseEntity.ok(sService.getCount());
    }

    //GET http://localhost:8080/student/list/list_by_first_char?char={firstChar}
    @GetMapping("/list/list_by_first_char")
    public ResponseEntity<List<String>> getListOfStudentsByFirstCharOfName(@RequestParam String firstChar){
        if(Objects.isNull(firstChar) || firstChar.isEmpty()){
            return ResponseEntity.ok(sService.getStudentsNamesByFirstChar());
        }
        return ResponseEntity.ok(sService.getStudentsNamesByFirstChar(firstChar));
    }

    //GET http://localhost:8080/student/list/console_print
    @GetMapping("/list/console_print")
    public ResponseEntity<Boolean> consolePrint(){
        sService.printingStudentsParallel();
        return ResponseEntity.ok(true);
    }

    //GET http://localhost:8080/student/list/console_print_sync
    @GetMapping("/list/console_print_sync")
    public ResponseEntity<Boolean> consolePrintSync(){
        sService.printingStudentsParallelSync();
        return ResponseEntity.ok(true);
    }
}

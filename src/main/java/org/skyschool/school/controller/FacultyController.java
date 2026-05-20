package org.skyschool.school.controller;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    @Autowired
    private FacultyService facultyService;


    //GET http://localhost:8080/faculty/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(faculty);
    }

    //GET http://localhost:8080/faculty/{facultyId}/students
    @GetMapping("/{facultyId}/students")
    public ResponseEntity<Set<Student>> getStudentsFromFaculty(@PathVariable Long facultyId) {
        Set<Student> students = facultyService.findStudentsInFaculty(facultyId);
        if (students == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(students);
    }

    //GET http://localhost:8080/faculty
    @GetMapping
    public ResponseEntity<Set<Faculty>> getFaculties() {
        return ResponseEntity.ok(facultyService.getAll());
    }

    //PUT http://localhost:8080/faculty
    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.findFaculty(faculty.getId());
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyService.editFaculty(faculty));
    }

    //PUT http://localhost:8080/faculty/{facultyId}/student/{studentId}
    @PutMapping("{facultyId}/student/{studentId}")
    public ResponseEntity<Long> addStudentToFaculty(
            @PathVariable long facultyId,
            @PathVariable long studentId) {

        if (studentId < 0 || facultyId < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!facultyService.checkEntities(studentId, facultyId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (facultyService.addRelationship(studentId, facultyId)) {
            return ResponseEntity.ok(studentId);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    //GET http://localhost:8080/faculty/color/{color}
    @GetMapping("/color/{color}")
    public ResponseEntity<Set<Faculty>> getFacultyByColor(@PathVariable String color) {
        Set<Faculty> foundFaculties = facultyService.findFacultyByColor(color);
        if (foundFaculties.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculties);
    }


    //POST http://localhost:8080/faculty
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.addFaculty(faculty));
    }

    //DELETE http://localhost:8080/faculty/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        Faculty deletingFaculty = facultyService.findFaculty(id);
        boolean checkRemove = facultyService.deleteFaculty(id);
        if(!checkRemove){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //GET http://localhost:8080/faculty/name/getLongest
    @GetMapping("/name/getLongest")
    public ResponseEntity<String> getLongestName(){
        return ResponseEntity.ok(facultyService.getLongestFacultyName());
    }
}

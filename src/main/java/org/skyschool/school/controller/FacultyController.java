package org.skyschool.school.controller;

import org.skyschool.school.model.Faculty;
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

    //GET http://localhost:8080/faculty
    @GetMapping
    public ResponseEntity<Set<Faculty>> getFacuties(){
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

    //GET http://localhost:8080/faculty/color/{color}
    @GetMapping("/color/{color}")
    public ResponseEntity<Set<Faculty>> getFacultyByColor(@PathVariable String color) {
        Set<Faculty> findFaculty = facultyService.findFacultyByColor(color);
        if (findFaculty.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(findFaculty);
    }

    //POST http://localhost:8080/faculty
    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    //DELETE http://localhost:8080/faculty/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        Faculty deletingFaculty = facultyService.findFaculty(id);
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(deletingFaculty);
    }
}

package org.skyschool.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.FacultyRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.skyschool.school.service.FacultyService;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.assertj.core.api.Assertions;

import java.util.HashSet;

@DisplayName("Testing Students's controller")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyContorollerTestWithDB {
    @Autowired
    private FacultyService fService;
    @Autowired
    private FacultyRepository fRepository;
    @Autowired
    private StudentService sService;
    @Autowired
    private StudentsRepository sRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private Student testStudent;
    private Faculty testFaculty;
    private Long testStudentId;
    private Long testFacultyId;

    @BeforeEach
    void SetUp() {
        sRepository.deleteAll();
        fRepository.deleteAll();
        testStudent = new Student();
        testFaculty = new Faculty();
        testStudent.setAge(20);
        testStudent.setName("test student test");
        testFaculty.setName("test faculty test");
        testFaculty.setColor("red");
        sRepository.save(testStudent);
        fRepository.save(testFaculty);
        testStudentId = testStudent.getId();
        testFacultyId = testFaculty.getId();
    }

    @Test
    @DisplayName("Testing GET method for getting Faculty entity by ID")
    public void testingGettingFacultyEntityById() {
        ResponseEntity<Faculty> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + testFacultyId, Faculty.class);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo(testFaculty);
    }

    @Test
    @DisplayName("Testing GET method for getting collection of Faculty entity's Student entities")
    public void testingGettingStudentsFormFaculty() {
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName("test2 student test2");
            student.setAge(20);
            sRepository.save(student);
            sService.addRelationship(student.getId(), testFacultyId);
        }
        ResponseEntity<HashSet<Student>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + testFacultyId + "/students", HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Student>>() {
        });
        Assertions.assertThat(response.getBody().size()).isGreaterThan(1);
        Assertions.assertThat(response.getBody().size()).isLessThan(6);
        Assertions.assertThat(response.getBody().contains(testStudent)).isFalse();
        for (Student s : response.getBody()) {
            Assertions.assertThat(s.getName()).isEqualTo("test2 student test2");
            Assertions.assertThat(s.getAge()).isEqualTo(20);
            Assertions.assertThat(s.getFaculty()).isNotNull();
        }
    }

    @Test
    @DisplayName("Testing GET method for getting all Faculty entities")
    public void testingGettingAllFaculties() {
        for (int i = 0; i < 5; i++) {
            Faculty faculty = new Faculty();
            faculty.setName("test2 faculty test2" + i);
            faculty.setColor("red" + i);
            fRepository.save(faculty);
        }
        ResponseEntity<HashSet<Faculty>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty", HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Faculty>>() {
        });
        Assertions.assertThat(response.getBody().size()).isGreaterThan(1);
        Assertions.assertThat(response.getBody().size()).isLessThan(7);
        Assertions.assertThat(response.getBody().contains(testFaculty)).isTrue();
        for (Faculty f : response.getBody()) {
            System.out.println(f);
        }
    }

    @Test
    @DisplayName("Testing PUT method for editing faculty by Faculty entity inside body")
    public void testingEditingFacultyByFacultyEntityInBody() {
        Faculty oldFaculty = testFaculty;
        Faculty newFaculty = new Faculty();
        newFaculty.setColor("blue");
        newFaculty.setName("test3 faculty test3");
        newFaculty.setId(testFacultyId);
        HttpEntity<Faculty> facultyHttp = new HttpEntity<>(newFaculty);
        ResponseEntity<Faculty> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty", HttpMethod.PUT, facultyHttp, Faculty.class);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEqualTo(oldFaculty);
    }

    @Test
    @DisplayName("Testing PUT method for creation Student-Faculty relationship")
    public void testingCreationStudentFacultyRelationship() {
        ResponseEntity<Long> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + testFacultyId + "/student/" + testStudentId, HttpMethod.PUT, null, Long.class);
        System.out.println(response.getBody());
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo(testStudentId);
        Assertions.assertThat(sRepository.findById(response.getBody()).orElse(null).getFaculty()).isNotNull();
        Assertions.assertThat(sRepository.findById(response.getBody()).orElse(null).getFaculty()).isEqualTo(testFaculty);
    }

    @Test
    @DisplayName("Testing GET method for getting collection of Faculty entities by color")
    public void testingGettingFacultiesByColor() {
        String testColor = "blue";
        for (int i = 0; i < 5; i++) {
            Faculty f = new Faculty();
            f.setColor("blue");
            f.setName("test2 faculty test2" + i);
            fRepository.save(f);
        }
        for (int i = 0; i < 5; i++) {
            Faculty f = new Faculty();
            f.setName("test3 faculty test3" + i);
            f.setColor("red");
            fRepository.save(f);
        }
        ResponseEntity<HashSet<Faculty>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty/color/" + testColor, HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Faculty>>() {
        });
        Assertions.assertThat(response.getBody().size()).isGreaterThan(1);
        Assertions.assertThat(response.getBody().size()).isLessThan(6);
        for (Faculty f : response.getBody()) {
            Assertions.assertThat(f.getColor()).isEqualTo(testColor);
        }
    }

    @Test
    @DisplayName("Testing POST method for creation Faculty entity")
    public void testingCreationFacultyEntity(){
        Faculty f = new Faculty();
        f.setColor("blue");
        f.setName("test2 faculty test2");
        HttpEntity<Faculty> facultyHttp = new HttpEntity<>(f);
        ResponseEntity<Faculty> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty", HttpMethod.POST, facultyHttp, Faculty.class);
        Assertions.assertThat(response.getBody().getId()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotEqualTo(0L);
        Assertions.assertThat(response.getBody()).isNotEqualTo(testFaculty);
    }

    @Test
    @DisplayName("Testing DELETE method for removing Faculty entity from DB")
    public void testingDeletingFacultyFromDB(){
        ResponseEntity<Faculty> response = this.testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + testFacultyId, HttpMethod.DELETE, null, Faculty.class);
        Assertions.assertThat(fRepository.existsById(testFacultyId)).isFalse();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


}

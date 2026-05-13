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
public class StudentControllerTestWithDb {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentService sService;
    @Autowired
    private FacultyService fService;
    @Autowired
    private StudentsRepository sRepository;
    @Autowired
    private FacultyRepository fRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    private Student testStudent;
    private Faculty testFaculty;
    private Long testStudentId;
    private Long testFacultyId;

    @BeforeEach
    void SetUp() {
        sRepository.deleteAll();
        fRepository.deleteAll();
        testStudent = new Student();
        testStudent.setAge(20);
        testStudent.setName("test test test");
        testFaculty = new Faculty();
        testFaculty.setColor("red");
        testFaculty.setName("testFaculty");
        sRepository.save(testStudent);
        fRepository.save(testFaculty);
        testStudentId = testStudent.getId();
        testFacultyId = testFaculty.getId();
    }

    @Test
    @DisplayName("Testing GET method for getting student entity from BD")
    public void testingGettingStudentById() {
        ResponseEntity<Student> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/student/ " + testStudentId, Student.class);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo(testStudent);

    }

    @Test
    @DisplayName("Testing GET method for getting collection with all Student entities")
    public void testingGettingCollectionOfStudents() {
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName("test test test");
            student.setAge(20);
            sRepository.save(student);
        }
        ResponseEntity<HashSet<Student>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Student>>() {
        });
        Assertions.assertThat(response.getBody().size()).isEqualTo(6);
    }

    @Test
    @DisplayName("Testing GET method for getting student's faculty by student's id")
    public void testingGettingStudentFacultyByStudentId() {
        sService.addRelationship(testStudentId, testFacultyId);
        ResponseEntity<Faculty> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + testStudentId + "/faculty", Faculty.class);
        System.out.println(response.getBody());
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isEqualTo(testFaculty);
    }

    @Test
    @DisplayName("Testing GET method for founding Student entity by Age field")
    public void testingGettingStudentByAge() {
        int testAge = 30;
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName("test2 student test2");
            student.setAge(40);
            sRepository.save(student);
        }
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName("test3 student test3");
            student.setAge(30);
            sRepository.save(student);
        }
        ResponseEntity<HashSet<Student>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student/age/" + testAge, HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Student>>() {
        });
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().size()).isGreaterThan(1);
        for (Student s : response.getBody()) {
            Assertions.assertThat(s.getAge()).isEqualTo(testAge);
        }
    }

    @Test
    @DisplayName("Testing PUT method for updating Student entity")
    public void testingUpdatingStudentEntity() {
        Student originalStudent = testStudent;
        Student newStudent = new Student();
        newStudent.setName("test2 test2 test2");
        newStudent.setAge(30);
        newStudent.setId(testStudentId);
        HttpEntity<Student> studentEntity = new HttpEntity<>(newStudent);
        ResponseEntity<Student> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.PUT, studentEntity, Student.class);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(newStudent.getName());
        Assertions.assertThat(response.getBody()).isNotEqualTo(originalStudent);
    }

    @Test
    @DisplayName("Testing PUT method for creating relationship between Student and Faculty Entities")
    public void testingCreationRelationshipBetweenStudentAndFaculty() {
        ResponseEntity<Student> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student/" + testStudentId + "/faculty/" + testFacultyId, HttpMethod.PUT, null, Student.class);
        Assertions.assertThat(response.getBody()).isEqualTo(testStudent);
        Assertions.assertThat(response.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(response.getBody().getFaculty()).isEqualTo(testFaculty);
    }

    @Test
    @DisplayName("Testing POST method for creating Student entity")
    public void testingCreatingStudentEntity() {
        Student newStudent = new Student();
        newStudent.setAge(40);
        newStudent.setName("test3 test3 test3");
        HttpEntity<Student> studentHttp = new HttpEntity<>(newStudent);
        ResponseEntity<Student> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.POST, studentHttp, Student.class);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(newStudent.getName());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(newStudent.getAge());
    }

    @Test
    @DisplayName("Testing DELETE method for deleting Student entity")
    public void testingDeletingStudentEntity() {
        ResponseEntity<Void> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student/" + testStudentId, HttpMethod.DELETE, null, Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(sRepository.findById(testStudentId).orElse(null)).isNull();
    }

    @Test
    @DisplayName("Testing GET method for getting collection of Student entity by age range")
    public void testingGetStudentCollectionByAgeRange() {
        int min = 30;
        int max = 35;
        for (int i = 28; i < 39; i++) {
            Student student = new Student();
            student.setName("test age range");
            student.setAge(i);
            sRepository.save(student);
        }
        ResponseEntity<HashSet<Student>> response = this.testRestTemplate.exchange("http://localhost:" + port + "/student/age/range?min=" + min + "&max=" + max, HttpMethod.GET, null, new ParameterizedTypeReference<HashSet<Student>>() {
        });
        for (Student s : response.getBody()) {
            Assertions.assertThat(s.getAge()).isGreaterThanOrEqualTo(min);
            Assertions.assertThat(s.getAge()).isLessThanOrEqualTo(max);
        }
    }
}

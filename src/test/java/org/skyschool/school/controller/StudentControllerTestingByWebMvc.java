package org.skyschool.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.StudentsRepository.*;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.HashSet;
import java.util.Set;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@DisplayName("Testing StudentController throw WebMvcTest")
public class StudentControllerTestingByWebMvc {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StudentService sService;

    private Student testStudent;
    private Faculty testFaculty;

    private Long testStudentId = 101L;
    private Long testFactoryId = 202L;

    @BeforeEach
    void SetUp() {
        testStudent = new Student();
        testFaculty = new Faculty();
        testStudent.setName("test student test");
        testStudent.setAge(20);
        testStudent.setId(testStudentId);
        testFaculty.setName("test faculty test");
        testFaculty.setColor("red");
        testFaculty.setId(testFactoryId);
    }

    @Test
    @DisplayName("Testing GET method for getting student by ID with WebMvcTest")
    public void testingGettingStudentById() throws Exception {
        when(sService.findStudent(testStudentId)).thenReturn(testStudent);
        mockMvc.perform(get("/student/{id}", testStudentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudentId))
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Testing GET method for getting all students throw WebMvcTest")
    public void testingGettingAllStudentsWithWebMvcTest() throws Exception {
        HashSet<Student> students = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Student s = new Student();
            s.setId(303L + i);
            s.setAge(20 + i);
            s.setName("test2 student test2" + i);
            students.add(s);
        }

        when(sService.getStudents()).thenReturn(students);
        MvcResult result = mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(students.size()))
                .andReturn();
        Set<Student> returnedStudents = Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Student[].class));
        Assertions.assertThat(students.containsAll(returnedStudents)).isTrue();
        Assertions.assertThat(students.size()).isEqualTo(returnedStudents.size());
    }

    @Test
    @DisplayName("Testing GET method for getting Faculty entity from Student entity")
    public void testingGettingFacultyFromStudent() throws Exception {
        when(sService.getFacultyFromStudent(testStudentId)).thenReturn(testFaculty);
        mockMvc.perform(get("/student/{id}/faculty", testStudentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFactoryId))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Testing GET method for getting Student entity collection by age")
    public void testingGettingStudentsByAge() throws Exception {
        Set<Student> students = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Student s = new Student();
            s.setName("test2 student test2" + i);
            s.setAge(30);
            s.setId(303L + i);
            students.add(s);
        }
        when(sService.findStudentByAge(30)).thenReturn(students);
        MvcResult result = mockMvc.perform(get("/student/age/{age}", 30))
                .andExpect(status().isOk())
                .andReturn();
        Set<Student> resultStudents = Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Student[].class));
        Assertions.assertThat(students.containsAll(resultStudents)).isTrue();
        Assertions.assertThat(students.size()).isEqualTo(resultStudents.size());
    }

    @Test
    @DisplayName("Testing PUT method for editing Student entity")
    public void testingEditingStudentEntity() throws Exception {
        Student updatedStudent = new Student();
        updatedStudent.setId(testStudentId);
        updatedStudent.setAge(33);
        updatedStudent.setName("test2 student test2");
        when(sService.findStudent(testStudentId)).thenReturn(testStudent);
        when(sService.editStudent(updatedStudent)).thenReturn(updatedStudent);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudentId))
                .andExpect(jsonPath("$.name").value(updatedStudent.getName()))
                .andExpect(jsonPath("$.age").value(updatedStudent.getAge()));
    }

    @Test
    @DisplayName("Testing PUT method for relationship between Student and Faculty entities")
    public void testingCreationStudentFacultyRelationship() throws Exception {
        testStudent.setFaculty(testFaculty);
        when(sService.checkEntities(testStudentId, testFactoryId)).thenReturn(true);
        when(sService.addRelationship(testStudentId, testFactoryId)).thenReturn(true);
        when(sService.findStudent(testStudentId)).thenReturn(testStudent);

        mockMvc.perform(put("/student/{studentId}/faculty/{facultyId}", testStudentId, testFactoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudentId))
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()))
                .andExpect(jsonPath("$.faculty").exists())
                .andExpect(jsonPath("$.faculty.id").value(testFactoryId))
                .andExpect(jsonPath("$.faculty.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.faculty.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Testing POST method for creating new Student entity")
    public void testingCreationNewStudent() throws Exception {
        Student newStudent = new Student();
        newStudent.setName(testStudent.getName());
        newStudent.setAge(testStudent.getAge());
        when(sService.addStudent(newStudent)).thenReturn(testStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStudentId))
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    @DisplayName("Testing DELETE method for removing Student entity")
    public void testingRemoveStudentEntity() throws Exception {
        when(sService.removeStudent(testStudentId)).thenReturn(true);

        mockMvc.perform(delete("/student/{id}", testStudentId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Testing GET method for getting collection of Student entities by age range field")
    public void testingGettingCollectionOfStudentsByAgeRange() throws Exception {
        Set<Student> studentsByAge = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Student s = new Student();
            s.setId(202L + i);
            s.setName("test2 student test2" + i);
            s.setAge(30 + i);
            studentsByAge.add(s);
        }
        when(sService.findStudentsByRange(30, 35)).thenReturn(studentsByAge);
        MvcResult result = mockMvc.perform(get("/student/age/range?min={min}&max={max}", 30, 35))
                .andExpect(status().isOk())
                .andReturn();
        Set<Student> responseStudents = Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Student[].class));
        Assertions.assertThat(responseStudents).isNotNull();
        Assertions.assertThat(responseStudents).isNotEmpty();
        Assertions.assertThat(responseStudents.size()).isEqualTo(studentsByAge.size());
    }
}



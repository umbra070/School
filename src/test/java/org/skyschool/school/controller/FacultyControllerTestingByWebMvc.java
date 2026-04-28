package org.skyschool.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.StudentsRepository.*;
import org.skyschool.school.service.FacultyService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
@DisplayName("Testing FacultyController throw WebMvcTest")
public class FacultyControllerTestingByWebMvc {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FacultyService fService;

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
    @DisplayName("Testing GET method for getting Faculty entity by id")
    public void testingGettingFacultyById() throws Exception {
        when(fService.findFaculty(testFactoryId)).thenReturn(testFaculty);
        mockMvc.perform(get("/faculty/{id}", testFactoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFactoryId))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Testing GET method for getting Student entities collection from Faculty entity")
    public void testingGettingStudentsCollectionFromFaculty() throws Exception {
        for (int i = 0; i < 5; i++) {
            Student s = new Student();
            s.setId(303L + i);
            s.setName("test3 faculty test3" + i);
            s.setAge(30 + i);
            testFaculty.addStudent(s);
        }
        when(fService.findStudentsInFaculty(testFactoryId)).thenReturn(testFaculty.getStudents());
        MvcResult result = mockMvc.perform(get("/faculty/{facultyId}/students", testFactoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(testFaculty.getStudents().size()))
                .andReturn();
        Set<Student> students = Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Student[].class));
        Assertions.assertThat(students.size()).isEqualTo(testFaculty.getStudents().size());
    }

    @Test
    @DisplayName("Testing GET method for getting all Faculty entities collection")
    public void testingGettingFacultiesCollection() throws Exception {
        HashSet<Faculty> faculties = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Faculty f = new Faculty();
            f.setId(303L + i);
            f.setName("test2 faculty test2" + i);
            f.setColor("red");
            faculties.add(f);
        }
        when(fService.getAll()).thenReturn(faculties);
        MvcResult result = mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(faculties.size()))
                .andReturn();
        Assertions.assertThat(Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Faculty[].class)).size()).isEqualTo(faculties.size());
    }

    @Test
    @DisplayName("Testing PUT method for creation relationship between Student and Faculty entities")
    public void testingCreationFacultyStudentRelationship() throws Exception {
        when(fService.addRelationship(testStudentId, testFactoryId)).thenReturn(true);
        when(fService.checkEntities(testStudentId, testFactoryId)).thenReturn(true);
        mockMvc.perform(put("/faculty/" + testFactoryId + "/student/" + testStudentId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(testStudentId)));
    }

    @Test
    @DisplayName("Testing GET method for getting Faculty entities collection by Color field")
    public void testingGettingFacultiesByColor() throws Exception {
        Set<Faculty> faculties = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Faculty f = new Faculty();
            f.setColor("red");
            f.setName("test2 faculty test2" + i);
            f.setId(303L + i);
            faculties.add(f);
        }
        when(fService.findFacultyByColor("red")).thenReturn(faculties);
        MvcResult result = mockMvc.perform(get("/faculty/color/{color}", "red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(faculties.size()))
                .andReturn();
        Assertions.assertThat(Set.of(objectMapper.readValue(result.getResponse().getContentAsString(), Faculty[].class)).size()).isEqualTo(faculties.size());
    }

    @Test
    @DisplayName("Testing POST method for creation Faculty entity")
    public void testingCreationFacultyEntity() throws Exception {
        Faculty f = new Faculty(testFaculty.getName(), testFaculty.getColor());
        when(fService.addFaculty(f)).thenReturn(testFaculty);
        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFactoryId))
                .andExpect(jsonPath("$.name").value(testFaculty.getName()))
                .andExpect(jsonPath("$.color").value(testFaculty.getColor()));
    }

    @Test
    @DisplayName("Testing DELETE method for deleting Faculty entity")
    public void testingDeletingFacultyEntity() throws Exception{
        when(fService.deleteFaculty(testFactoryId)).thenReturn(true);
        mockMvc.perform(delete("/faculty/{facultyId}", testFactoryId))
                .andExpect(status().isNoContent());
    }
}

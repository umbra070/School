package org.skyschool.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyschool.school.model.Avatar;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.AvatarRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.skyschool.school.service.AvatarService;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.assertj.core.api.Assertions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@DisplayName("Avatar endpoints test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AvatarControllerTestWithDb {
    @Autowired
    private AvatarService aService;
    @Autowired
    private StudentService sService;
    @Autowired
    private AvatarRepository aRepository;
    @Autowired
    private StudentsRepository sRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private Student testStudent;
    private Avatar testAvatar;
    private long testStudentId;
    private MockMultipartFile testMultiPartFile;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        aRepository.deleteAll();
        sRepository.deleteAll();

        testStudent = new Student();
        testStudent.setName("Test");
        testStudent.setAge(20);
        sRepository.save(testStudent);
        testStudentId = testStudent.getId();

        testAvatar = new Avatar();
        testAvatar.setStudent(testStudent);
        testAvatar.setMediaType("image/jpeg");
        testAvatar.setData(createTestImage(1920, 1080, "jpg"));
        testAvatar.setFileSize((long) createTestImage(1920, 1080, "jpg").length);
        testAvatar.setFileName(testStudent.getId().toString() + ".jpg");

        testMultiPartFile = new MockMultipartFile("file", testAvatar.getFileName(), testAvatar.getMediaType(), testAvatar.getData());
    }

    @Test
    @DisplayName("Testing GET method for success getting small student's picture(preview) from DB")
    public void testGetStudentAvatarSmall() throws Exception {
        aService.saveAvatar(testStudentId, testMultiPartFile);
        ResponseEntity<byte[]> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/avatar/" + testStudentId + "/small", byte[].class);
        System.out.println("port:" + port);
        System.out.println("response: " + response.getStatusCode());
        System.out.println("response has body:" + response.hasBody());
        System.out.println("response headers: " + response.getHeaders());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().length).isGreaterThan(0);
        aService.deleteAvatar(testStudentId);
    }

    @Test
    @DisplayName("Testing GET method for unsuccess getting small student's picture(preview) from DB")
    public void testUnsuccessGetStudentAvatarSmall() {
        ResponseEntity<byte[]> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/avatar/" + testStudentId + "/small", byte[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Testing GET method for success getting full student's picture(avatar) from directory")
    public void testGetStudentAvatarFull() throws Exception {
        aService.saveAvatar(testStudentId, testMultiPartFile);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/avatar/" + testStudentId + "/full", byte[].class))
                .isNotNull();
        aService.deleteAvatar(testStudentId);
    }

    @Test
    @DisplayName("Testing GET method for unsuccess getting full student's picture(avatar) from directory")
    public void testUnsuccessGetStudentAvatarFull() throws Exception {
        ResponseEntity<byte[]> response = this.testRestTemplate.getForEntity("http://localhost:" + port + "/avatar/" + testStudentId + "/full", byte[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(response.getBody()).toString().contains("Avatar not found");
    }

    @Test
    @DisplayName("Testing success POST method with testing picture")
    public void testSuccessPostAvatar() throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(testMultiPartFile.getBytes()) {
            @Override
            public String getFilename() {
                return testMultiPartFile.getOriginalFilename();
            }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = this.testRestTemplate.postForEntity("http://localhost:" + port + "/avatar/" + testStudentId, requestEntity, String.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        aService.deleteAvatar(testStudentId);
    }

    @Test
    @DisplayName("Test success DELETE method with testing picture avatar")
    public void testSuccessDeletingPicture() throws Exception {
        aService.saveAvatar(testStudentId, testMultiPartFile);
        ResponseEntity<Void> responseEntity = this.testRestTemplate.exchange("http://localhost:" + port + "/avatar/" + testStudentId, HttpMethod.DELETE, null, void.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    //Содержит строковое представление jpeg изображения белого квадрата,
    //которое возвращается в виде последовательности байтов byte[]
    //для тестирования добавления аватара в файл и БД
//    private byte[] createSimpleTestImage() {
//        return Base64.getDecoder().decode(
//                "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigD//2Q=="
//        );
//    }
    private byte[] createTestImage(int width, int height, String format) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLUE);
            graphics.drawRect(10, 10, width - 20, height - 20);
            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test image", e);
        }
    }
}

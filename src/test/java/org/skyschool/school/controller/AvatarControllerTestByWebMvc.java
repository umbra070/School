package org.skyschool.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.skyschool.school.model.Avatar;
import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.StudentsRepository.*;
import org.skyschool.school.service.AvatarService;
import org.skyschool.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@WebMvcTest(AvatarController.class)
@DisplayName("Testing AvatarController by WebMvc")
public class AvatarControllerTestByWebMvc {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AvatarService aService;

    private Avatar testAvatar;
    private Student testStudent;
    private Long testAvatarId = 101L;
    private Long testStudentId = 202L;

    private byte[] testBytePic = createTestImage(1920, 1080, "jpg");
    private byte[] testByteSmallPic = createTestImage(192, 108, "jpg");

    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testStudent = new Student(testStudentId, "test student test", 20);
        testAvatar = new Avatar();
        testAvatar.setFileName("test.jpg");
        testAvatar.setData(testByteSmallPic);
        testAvatar.setFileSize((long) testByteSmallPic.length);
        testAvatar.setMediaType("image/jpeg");
        testAvatar.setId(testAvatarId);
        testAvatar.setStudent(testStudent);
        testFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                testBytePic
        );
    }

    @Test
    @DisplayName("Testing GET method for getting small picture(preview)")
    public void testingGettingSmallPicture() throws Exception {
        when(aService.findAvatar(testStudentId)).thenReturn(testAvatar);
        MvcResult result = mockMvc.perform(get("/avatar/{studentId}/small", testStudentId))
                .andExpect(status().isOk())
                .andReturn();
        byte[] pic = result.getResponse().getContentAsByteArray();
        Assertions.assertThat(pic.length).isEqualTo(testByteSmallPic.length);
    }

    @Test
    @DisplayName("Testing GET method for getting full picture")
    public void testingGettingFullPicture() throws Exception {
        when(aService.findAvatar(testStudentId)).thenReturn(testAvatar);
        when(aService.getFullPath(testAvatar)).thenReturn(Path.of("/fake/path.jpg"));

        try (var filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newInputStream(any(Path.class)))
                    .thenReturn(new ByteArrayInputStream(testBytePic));

            MockHttpServletResponse response = mockMvc.perform(get("/avatar/{id}/full", testStudentId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/jpeg"))
                    .andReturn()
                    .getResponse();
        }
    }

    //Генерирует тестовое изображение заданных параметров,
    //которое возвращается в виде последовательности байтов byte[]
    //для тестирования добавления аватара в файл и БД
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

    @Test
    @DisplayName("Testing POST method for uploading avatar")
    void upLoadAvatar_Success() throws Exception {
        when(aService.saveAvatar(eq(testStudentId), any())).thenReturn(true);

        mockMvc.perform(multipart(HttpMethod.POST, "/avatar/{studentId}", testStudentId)
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(content().string(testStudentId.toString()));
    }

    @Test
    @DisplayName("Testing DELETE method for deleting avatar")
    public void testingDeletingAvatar() throws Exception{
        when(aService.deleteAvatar(testStudentId)).thenReturn(true);
        mockMvc.perform(delete("/avatar/{studentId}", testStudentId))
                .andExpect(status().isNoContent());
    }
}
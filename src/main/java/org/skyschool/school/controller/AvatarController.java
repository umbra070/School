package org.skyschool.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.skyschool.school.model.Avatar;
import org.skyschool.school.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    @Autowired
    private AvatarService service;
    @Value("#{${avatar.size.max.kb}*1024}")
    private long fileSizeLimit;

    //GET http://localhost:8080/avatar/{studentId}/small
    @GetMapping("/{studentId}/small")
    public ResponseEntity<byte[]> getAvatarSmallPic(@PathVariable Long studentId) {
        Avatar foundAvatar = service.findAvatar(studentId);
        if (foundAvatar == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(foundAvatar.getData().length);
        headers.setContentType(MediaType.parseMediaType(foundAvatar.getMediaType()));
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(foundAvatar.getData());
    }

    //GET http://localhost:8080/avatar/{studentId}/full
    @GetMapping("/{studentId}/full")
    public void getAvatarFullPic(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        Avatar foundAvatar = service.findAvatar(studentId);
        if (foundAvatar == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Avatar not found!");
            return;
        }
        if (service.getFullPath(foundAvatar) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found!");
            return;
        }
        Path path = service.getFullPath(foundAvatar);
        try (
                InputStream is = Files.newInputStream(path);
                OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(foundAvatar.getMediaType());
            response.setContentLength(foundAvatar.getFileSize().intValue());
            is.transferTo(os);
        }
    }

    //POST http://localhost:8080/avatar/{studentId}
    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upLoadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        if (file.getSize() > fileSizeLimit) {
            return ResponseEntity.badRequest().body("File is larger than limit " + fileSizeLimit + " bytes");
        }
        if (service.saveAvatar(studentId, file)) {
            return ResponseEntity.ok().body(studentId.toString());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student with those ID " + studentId + " not found");
        }
    }

    //GET http://localhost:8080/avatar/page?page={page}&size={size}
    @GetMapping("/page")
    public ResponseEntity<Set<Avatar>> getAvatarsPage(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(service.getAvatarsPage(page, size));
    }


    //DELETE http://localhost:8080/avatar/{studentId}
    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long studentId) throws IOException {
        if (!service.deleteAvatar(studentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid student ID or avatar not found");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Success");
    }
}

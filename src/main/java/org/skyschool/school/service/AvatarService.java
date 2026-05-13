package org.skyschool.school.service;

import org.skyschool.school.model.Avatar;
import org.skyschool.school.model.Student;
import org.skyschool.school.repos.AvatarRepository;
import org.skyschool.school.repos.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {
    private static final int BUFFER_SIZE = 1024;

    @Autowired
    private AvatarRepository aRepository;
    @Autowired
    private StudentsRepository sRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarPath;

    @Value("${avatar.scaling.db}")
    private int avatarScaling;
    @Value("${avatar.height.min}")
    private int heightMin;
    @Value("${avatar.width.min}")
    private int widthMin;
    @Value("${avatar.page.size.min}")
    private int pageMinSize;
    @Value("${avatar.page.size.max}")
    private int pageMaxSize;

    @Transactional
    public Avatar findAvatar(Long studentId) {
        return aRepository.findAvatarByStudentId(studentId).orElse(null);
    }

    public Path getFullPath(Avatar avatar) {
        if (avatar == null || avatar.getFileName() == null) {
            return null;
        }
        return Path.of(avatarPath, avatar.getFileName());
    }

    @Transactional
    public boolean saveAvatar(Long studentId, MultipartFile file) throws IOException {
        Student foundStudent = sRepository.findById(studentId).orElse(null);
        if (foundStudent == null) {
            return false;
        }
        Avatar foundAvatar = aRepository.findAvatarByStudentId(studentId).orElse(new Avatar());
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String fileName = studentId + "." + extension;
        Path filePath = Path.of(avatarPath, fileName);
        Files.createDirectories(filePath.getParent());
        if (foundAvatar.getFileName() != null) {
            Files.deleteIfExists(Path.of(avatarPath, foundAvatar.getFileName()));
        }
        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
                BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);
        ) {
            bis.transferTo(bos);
        }
        foundAvatar.setFileName(fileName);
        foundAvatar.setMediaType(file.getContentType());
        foundAvatar.setStudent(foundStudent);
        foundAvatar.setFileSize(file.getSize());
        foundAvatar.setData(minimizePic(file.getBytes(), extension));
        aRepository.save(foundAvatar);
        return true;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    @Transactional
    public boolean deleteAvatar(Long studentId) throws IOException {
        Avatar foundAvatar = aRepository.findAvatarByStudentId(studentId).orElse(null);
        if (foundAvatar == null) {
            return false;
        }
        Files.deleteIfExists(Path.of(avatarPath, foundAvatar.getFileName()));
        aRepository.delete(foundAvatar);
        return true;
    }

    @Transactional
    public Set<Avatar> getAvatarsPage(int page, int size) {
        int avatarsCount = aRepository.totalAvatarsCount();
        System.out.println("totalAvatarsCount: " + avatarsCount);
        System.out.println("Original page: " + page + ", size: " + size);
        if (size < pageMinSize) {
            size = pageMinSize;
        }
        if (size > pageMaxSize) {
            size = pageMaxSize;
        }
        int maxPage = (avatarsCount - 1) / size;
        if (page > maxPage) {
            page = maxPage;
        }
        if (page < 0) {
            page = 0;
        }
        System.out.println("Corrected page: " + page + ", size: " + size);
        PageRequest pageRequest = PageRequest.of(page, size);
        return aRepository.findAll(pageRequest).toSet();
    }

    private byte[] minimizePic(byte[] data, String extension) throws IOException {
        byte[] newData;
        int height = heightMin;
        int width = widthMin;
        int ratio;
        System.out.println("original picture length:" + data.length);

        try (
                InputStream is = new ByteArrayInputStream(data);
                BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage pic = ImageIO.read(bis);
            int scaledWidth = pic.getWidth() / avatarScaling;
            int scaledHeight = pic.getHeight() / avatarScaling;

            if (scaledWidth < widthMin && scaledHeight < heightMin) {
                double scaleByWidth = (double) widthMin / pic.getWidth();
                double scaleByHeight = (double) heightMin / pic.getHeight();
                double scale = Math.max(scaleByWidth, scaleByHeight);

                width = (int) (pic.getWidth() * scale);
                height = (int) (pic.getHeight() * scale);
            } else if (scaledWidth < widthMin) {
                width = widthMin;
                height = (int) ((double) pic.getHeight() * width / pic.getWidth());
            } else if (scaledHeight < heightMin) {
                height = heightMin;
                width = (int) ((double) pic.getWidth() * height / pic.getHeight());
            } else {
                width = scaledWidth;
                height = scaledHeight;
            }

            BufferedImage smallPic = new BufferedImage(width, height, pic.getType());
            Graphics2D graphics = smallPic.createGraphics();
            graphics.drawImage(pic, 0, 0, width, height, null);
            graphics.dispose();
            ImageIO.write(smallPic, extension, baos);
            newData = baos.toByteArray();
        }
        System.out.println("minimize pic length:" + newData.length);
        return newData;
    }
}

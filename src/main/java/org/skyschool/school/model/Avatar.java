package org.skyschool.school.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Avatar {
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;
    private Long fileSize;
    private String mediaType;
    @Lob
    private byte[] data;
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Override
    public int hashCode() {
        return Objects.hash(fileSize, mediaType, fileName);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj.getClass() != this.getClass()){
            return false;
        }
        Avatar a = (Avatar) obj;
        if(!a.mediaType.equals(this.mediaType) || !Objects.equals(a.fileSize, this.fileSize)){
            return false;
        }
        return a.data == this.data;
    }

    @Override
    public String toString() {
        return String.format("id:%d|name:%s|size:%d|media type:%s");
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}



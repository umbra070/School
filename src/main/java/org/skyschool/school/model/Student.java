package org.skyschool.school.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Student {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;
    @ManyToOne
    @JoinColumn(name="faculty_id")
    private Faculty faculty;
    @OneToOne(mappedBy = "student")
    private Avatar avatar;

    public Student() {

    }
    public Student(String name, int age){
        this.name = name;
        this.age = age;
    }

    public Student(long id, String name, int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Student(long id, String name, int age, Faculty faculty) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.faculty = faculty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        if(this.id == null){
            return Objects.hash(name, age);
        }
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Student s = (Student) obj;
        if(s.id == null && this.id == null){
            return (this.name.equals(s.name) && this.age == s.age);
        }
        return (this.name.equals(s.name) && this.age == s.age && Objects.equals(this.id, s.id));
    }

    @Override
    public String toString() {
        if(faculty == null){
            return String.format("id:%d|name:%s|age:%d", id, name, age);
        }
        return String.format("id:%d|name:%s|age:%d|faculty:%s", id, name, age, faculty.getName());
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public boolean isFacultyPresent(){
        return this.faculty != null;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
}

package org.skyschool.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Faculty {
    @Id
    @GeneratedValue
    private Long id;
    private String name, color;
    @OneToMany(mappedBy = "faculty")
    private Set<Student> students;

    public Faculty() {

    }

    public Faculty(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        students = new HashSet<>();
    }

    public Set<Student> getStudents(){
        return students;
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

    public void addStudent(Student s) {
        students.add(s);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Faculty f = (Faculty) obj;
        return Objects.equals(this.id, f.id) && Objects.equals(this.name, f.name) && Objects.equals(this.color, f.color);
    }

    @Override
    public String toString() {
        return String.format("id:%d|name:%s|color:%s", id, name, color);
    }

    public void removeStudent(Student student){
        students.remove(student);
    }
}

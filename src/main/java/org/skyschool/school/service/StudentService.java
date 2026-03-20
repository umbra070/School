package org.skyschool.school.service;

import org.skyschool.school.model.Student;

import java.util.HashMap;

public class StudentService {
    private final HashMap<Long, Student> students = new HashMap<Long, Student>();
    private long count = 0;

    public Student addStudent(Student student) {
        student.setId(count++);
        return students.put(student.getId(), student);
    }

    public Student editStudent(Student student) {
        if (!students.containsKey(student.getId())) {
            return null;
        }
        return students.put(student.getId(), student);
    }

    public Student findStudent(long id) {
        return students.get(id);
    }

    public Student removeStudent(long id) {
        return students.remove(id);
    }
}

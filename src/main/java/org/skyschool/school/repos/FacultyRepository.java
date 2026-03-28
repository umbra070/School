package org.skyschool.school.repos;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f.students FROM Faculty f WHERE f.id=:facultyId")
    public Set<Student> getStudents(@Param("facultyId") Long facultyId);
}

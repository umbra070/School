package org.skyschool.school.repos;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.age BETWEEN :min AND :max")
    Set<Student> findStudentsByAgeRange(@Param("min") int min, @Param("max") int max);

    @Query("SELECT s.faculty FROM Student s WHERE s.id=:id")
    Optional<Faculty> getStudentFaculty(@Param("id") Long studentId);

    @Query("SELECT s FROM Student s WHERE s.age = :age")
    Set<Student> findStudentsByAge(@Param("age") int age);

    @Query("SELECT COUNT(*) FROM Student")
    Integer getStudentsCount();

    @Query("SELECT AVG(age) FROM Student")
    Integer getAverageAge();

    @Query("SELECT s FROM Student s ORDER BY s.id  DESC LIMIT 5")
    public Set<Student> getFiveLastStudents();

    @Query("SELECT COUNT(*) FROM Student")
    public int totalStudentsCount();
}

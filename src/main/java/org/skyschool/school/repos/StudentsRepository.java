package org.skyschool.school.repos;

import org.skyschool.school.model.Faculty;
import org.skyschool.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface StudentsRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.age BETWEEN :min AND :max")
    Set<Student> findStudentsByAgeRange(int min, int max);

    @Query("SELECT s.faculty FROM Student s WHERE s.id=:id")
    Optional<Faculty> getStudentFaculty(long id);
}

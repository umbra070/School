package org.skyschool.school.repos;

import org.skyschool.school.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    @Query("SELECT a FROM Avatar a WHERE a.student.id=:studentId")
    Optional<Avatar> findAvatarByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT a.data FROM Avatar a WHERE a.student.id=:studentId")
    Optional<byte[]> getSmallPic(@Param("studentId") Long studentId);
}

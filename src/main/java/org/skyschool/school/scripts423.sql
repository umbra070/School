SELECT s.name, s.age, f.name FROM student s LEFT JOIN faculty f on f.id = s.faculty_id;
SELECT s.name, s.age, a.file_name FROM student s INNER JOIN public.avatar a on s.id = a.student_id;
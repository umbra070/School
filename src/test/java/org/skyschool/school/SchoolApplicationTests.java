package org.skyschool.school;

import org.junit.jupiter.api.Test;
import org.skyschool.school.controller.AvatarController;
import org.skyschool.school.controller.FacultyController;
import org.skyschool.school.controller.StudentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchoolApplicationTests {
	@LocalServerPort
	private int port;

	@Autowired
	private StudentController sController;
	@Autowired
	private FacultyController fController;
	@Autowired
	private AvatarController aController;

	@Test
	void contextLoads() {

	}

}

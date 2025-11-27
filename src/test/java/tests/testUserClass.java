package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.User;

class testUserClass {

	@Test
	void test() {
        User user = new User("Yousef", "yousef@example.com");

        assertEquals("Yousef", user.getName());
        assertEquals("yousef@example.com", user.getEmail());
	}

}

package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Admin;

class testAdminClass {

	@Test
	void test() {
	    Admin admin = new Admin("admin", "123");

	    assertEquals("admin", admin.getUsername());
	    assertEquals("123", admin.getPassword());
	}

}

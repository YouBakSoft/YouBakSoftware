package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Admin;

class adminConstructorTest {

    @Test
    void createAdmin() {
        Admin admin = new Admin("admin", "1234");
        assertEquals("admin", admin.getUserName());
    }

}

package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Session;

class adminLogOutTest {

    @Test
    void logout() {
        Session.login("admin");
        Session.logout();
        assertFalse(Session.isLoggedIn());
    }

}

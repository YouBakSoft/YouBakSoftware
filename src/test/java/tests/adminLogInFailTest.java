package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Admin;

class adminLogInFailTest {

    @Test
    void loginFail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("wrongUser", "wrongPass");
        });
        assertEquals("Invalid Admin!", exception.getMessage());
    }
    
    //fails first when admin does not exist then pass

}

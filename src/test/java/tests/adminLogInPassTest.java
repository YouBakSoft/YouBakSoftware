package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import domain.Admin;

class adminLogInPassTest {

    @Test
    void loginSuccess() throws IOException {
        Admin.loginThrow("yousef", "1234"); 
    }

    //pass when the user exists
}

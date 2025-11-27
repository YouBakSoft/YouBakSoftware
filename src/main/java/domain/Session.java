package domain;

public class Session {
	
    private static boolean loggedIn = false;

    public static void login(String username) {
        loggedIn = true;
    }

    public static void logout() {
        loggedIn = false;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

}

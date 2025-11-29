package presentation;

public class ConsoleUtils {

    // ========== HEADER ==========
    public static void printHeader(String text) {
        System.out.println(ConsoleColors.BLUE + "===============================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  " + text + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + "===============================" + ConsoleColors.RESET);
    }

    // ========== CLEAR CONSOLE ==========
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                System.out.print("");
            System.out.flush();
        } catch (Exception ignored) {}
    }

    // ========== PADDING ==========
    public static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    public static String padLeft(String text, int width) {
        return String.format("%" + width + "s", text);
    }

    // ========== MENU ==========
    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println("[" + i + "] " + options[i]);
        }
    }
}

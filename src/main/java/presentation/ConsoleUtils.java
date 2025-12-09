package presentation;

import java.io.IOException;

/**
 * Utility class for common console operations such as printing headers,
 * clearing the console, padding text, and displaying menus.
 * 
 * <p>Example usage:
 * <pre><code>
 * ConsoleUtils.clearConsole();
 * ConsoleUtils.printHeader("Library System");
 * String padded = ConsoleUtils.padRight("Book", 20);
 * ConsoleUtils.printMenu(new String[]{"Add Book", "Borrow Book", "Exit"});
 * </code></pre>
 * </p>
 */
public class ConsoleUtils {

    /**
     * Prints a formatted header in the console using colored borders.
     *
     * @param text the header text to display
     */
    public static void printHeader(String text) {
        System.out.println(ConsoleColors.BLUE + "===============================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  " + text + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + "===============================" + ConsoleColors.RESET);
    }

    /**
     * Clears the console screen.
     * Works for Windows. On other OS, it attempts a blank output.
     */
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                System.out.print("");
            }
            System.out.flush();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }


    /**
     * Pads a string on the right with spaces to achieve the given width.
     *
     * @param text the text to pad
     * @param width the target width
     * @return the right-padded string
     */
    public static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    /**
     * Pads a string on the left with spaces to achieve the given width.
     *
     * @param text the text to pad
     * @param width the target width
     * @return the left-padded string
     */
    public static String padLeft(String text, int width) {
        return String.format("%" + width + "s", text);
    }

    /**
     * Prints an indexed menu of options to the console.
     *
     * @param options an array of menu options to display
     */
    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println("[" + i + "] " + options[i]);
        }
    }
}

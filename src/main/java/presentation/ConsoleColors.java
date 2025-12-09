package presentation;

/**
 * Utility class defining ANSI escape codes for coloring console output.
 * 
 * <p>Example usage:
 * <pre><code>
 * System.out.println(ConsoleColors.RED + "Error message" + ConsoleColors.RESET);
 * System.out.println(ConsoleColors.GREEN + "Success message" + ConsoleColors.RESET);
 * </code></pre>
 * </p>
 */
public class ConsoleColors {

    /** Resets the console color to default. */
    public static final String RESET = "\033[0m";  

    /** Red text color. */
    public static final String RED = "\033[0;31m";

    /** Green text color. */
    public static final String GREEN = "\033[0;32m";

    /** Yellow text color. */
    public static final String YELLOW = "\033[0;33m";

    /** Blue text color. */
    public static final String BLUE = "\033[0;34m";

    /** Purple text color. */
    public static final String PURPLE = "\033[0;35m";

    /** Cyan text color. */
    public static final String CYAN = "\033[0;36m";

    /** Bold white text color. */
    public static final String WHITE_BOLD = "\033[1;37m";
}

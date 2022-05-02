package jge.util;

public class Log {
    private static String loggerName = "JGE ";

    private static void print(String text, boolean isErr) {
        if (isErr) {
            System.err.println(loggerName + text);
        }
        else {
            System.out.println(loggerName + text);
        }
    }

    public static void info(String message) {
        print("INFO: " + message, false);
    }

    public static void error(String message) {
        print("ERROR: " + message, true);
    }

    // There is no RCE here :)
}

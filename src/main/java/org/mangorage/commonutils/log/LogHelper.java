package org.mangorage.commonutils.log;

public final class LogHelper {
    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        System.out.println("[ERROR] " + message);
    }
}

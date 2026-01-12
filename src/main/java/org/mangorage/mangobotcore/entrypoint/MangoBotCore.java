package org.mangorage.mangobotcore.entrypoint;

import org.mangorage.mangobotcore.internal.plugin.PluginManagerImpl;

public final class MangoBotCore {
    static String[] args;
    static boolean devMode = false;
    static boolean loaded = false;

    public static String[] getArgs() {
        return args;
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static void main(String[] args) {
        if (loaded) return;
        MangoBotCore.args = args;

        for (String arg : args) {
            if (arg.contains("--dev")) {
                devMode = true;
                break;
            }
        }

        PluginManagerImpl.INSTANCE.load();
        loaded = true;
    }
}

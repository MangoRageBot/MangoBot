package org.mangorage.entrypoint;

import org.mangorage.mangobotcore.plugin.internal.PluginManagerImpl;

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

    public static void main(String[] args) throws ClassNotFoundException {
        if (loaded) return;
        var a = MangoBotCore.class.getClassLoader().loadClass("kotlin.Unit");
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

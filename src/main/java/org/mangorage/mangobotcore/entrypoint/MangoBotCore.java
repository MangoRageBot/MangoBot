package org.mangorage.mangobotcore.entrypoint;

import org.mangorage.mangobotcore.internal.entrypoint.MangoBotEntrypoint;
import org.mangorage.mangobotcore.internal.plugin.PluginManagerImpl;

import java.util.List;

public final class MangoBotCore {

    @Deprecated
    public static String[] getArgs() {
        return MangoBotEntrypoint.args;
    }

    public static List<String> getStartingArgs() {
        return MangoBotEntrypoint.immutableArgs;
    }

    public static boolean isDevMode() {
        return MangoBotEntrypoint.devMode;
    }
}

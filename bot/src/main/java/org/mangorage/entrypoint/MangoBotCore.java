package org.mangorage.entrypoint;

import org.mangorage.mangobotcore.plugin.internal.PluginManagerImpl;

public final class MangoBotCore {
    static boolean loaded = false;

    public static void main(String[] args) {
        if (loaded) return;
        PluginManagerImpl.INSTANCE.load();
        loaded = true;
    }
}

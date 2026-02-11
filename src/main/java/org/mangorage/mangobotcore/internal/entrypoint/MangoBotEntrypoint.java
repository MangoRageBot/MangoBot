package org.mangorage.mangobotcore.internal.entrypoint;

import org.mangorage.bootstrap.api.launch.ILaunchTargetEntrypoint;
import org.mangorage.bootstrap.api.logging.IDeferredMangoLogger;
import org.mangorage.bootstrap.api.logging.ILoggerFactory;
import org.mangorage.bootstrap.api.logging.IMangoLogger;
import org.mangorage.mangobotcore.internal.plugin.PluginManagerImpl;

import java.util.Arrays;
import java.util.List;

public final class MangoBotEntrypoint implements ILaunchTargetEntrypoint {

    private static final IDeferredMangoLogger LOGGER = ILoggerFactory.getDefault().getWrappedProvider("slf4j", MangoBotEntrypoint.class);
    public static String[] args;
    public static boolean devMode = false;
    public static boolean loaded = false;
    public static List<String> immutableArgs;

    @Override
    public String getLaunchTargetId() {
        return "mangobot";
    }

    @Override
    public void init(String[] args) {
        if (loaded) return;
        LOGGER.get().info("Initializing MangoBot with args: {}", Arrays.toString(args));

        MangoBotEntrypoint.args = args;
        MangoBotEntrypoint.immutableArgs = List.of(args);

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

package org.mangorage.mangobotcore.api.plugin;

import org.mangorage.bootstrap.api.logging.IDeferredMangoLogger;
import org.mangorage.bootstrap.api.logging.ILoggerFactory;
import org.mangorage.bootstrap.api.logging.IMangoLogger;
import org.mangorage.mangobotcore.api.plugin.v1.MangoBotPlugin;
import org.mangorage.mangobotcore.api.plugin.v1.Plugin;
import org.mangorage.mangobotcore.internal.ExampleThing;

import static org.mangorage.mangobotcore.api.plugin.MangoBotCore.ID;

@MangoBotPlugin(id = ID)
public final class MangoBotCore implements Plugin {

    private static final IDeferredMangoLogger LOGGER = ILoggerFactory.getDefault().getWrappedProvider("slf4j", MangoBotCore.class);

    public static final String ID = "mangobotcore";

    public static boolean isDevMode() {
        return org.mangorage.mangobotcore.entrypoint.MangoBotCore.isDevMode();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void load() {
        new ExampleThing().load();
        LOGGER.get().info("Loaded MangoBotCore");
    }
}

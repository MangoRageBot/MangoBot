import org.mangorage.bootstrap.api.launch.ILaunchTargetEntrypoint;
import org.mangorage.bootstrap.api.transformer.IClassTransformer;
import org.mangorage.mangobotcore.api.plugin.v1.Plugin;
import org.mangorage.mangobotcore.api.plugin.v1.IPluginInfoGetter;
import org.mangorage.mangobotcore.api.plugin.MangoBotCore;
import org.mangorage.mangobotcore.internal.entrypoint.MangoBotEntrypoint;
import org.mangorage.mangobotcore.internal.transformer.ExampleTransformer;

module org.mangorage.mangobotcore {
    requires static org.jetbrains.annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;

    requires net.dv8tion.jda;
    requires org.mangorage.bootstrap;
    requires kotlin.stdlib;
    requires static org.spongepowered.mixin;

    requires com.google.gson;

    // Config API
    exports org.mangorage.mangobotcore.api.config.v1;

    // JDA API
    exports org.mangorage.mangobotcore.api.jda.command.v1;
    exports org.mangorage.mangobotcore.api.jda.event.v1;

    // Plugin API
    exports org.mangorage.mangobotcore.api.plugin;
    exports org.mangorage.mangobotcore.api.plugin.v1;

    // Common Utils
    exports org.mangorage.mangobotcore.api.util.data;
    exports org.mangorage.mangobotcore.api.util.jda;
    exports org.mangorage.mangobotcore.api.util.log;
    exports org.mangorage.mangobotcore.api.util.misc;

    // More JDA Utils
    exports org.mangorage.mangobotcore.api.util.jda.slash.command;
    exports org.mangorage.mangobotcore.api.util.jda.slash.command.watcher;
    exports org.mangorage.mangobotcore.api.util.jda.slash.component;
    exports org.mangorage.mangobotcore.api.util.jda.slash.component.interact;
    exports org.mangorage.mangobotcore.api.util.jda.slash.message;



    opens org.mangorage.mangobotcore.entrypoint; // Opens it
    opens org.mangorage.mangobotcore.internal.entrypoint to org.mangorage.bootstrap; // Opens it to bootstrap

    opens org.mangorage.mangobotcore.internal.plugin to com.google.gson; // GSON
    opens org.mangorage.mangobotcore.internal.plugin.dependency to com.google.gson; // GSON


    provides Plugin with MangoBotCore;
    provides IClassTransformer with ExampleTransformer;
    provides ILaunchTargetEntrypoint with MangoBotEntrypoint;

    uses Plugin;
    uses IPluginInfoGetter;
    uses IClassTransformer;
    uses ILaunchTargetEntrypoint;
}
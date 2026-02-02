module org.mangorage.mangobotcore {
    requires static org.jetbrains.annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;

    requires net.dv8tion.jda;
    requires org.mangorage.bootstrap;
    requires kotlin.stdlib;
    requires static org.spongepowered.mixin;

    requires com.google.gson;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;

    // Config API
    exports org.mangorage.mangobotcore.api.config.v1;

    // JDA API
    exports org.mangorage.mangobotcore.api.jda.command.v2;
    exports org.mangorage.mangobotcore.api.jda.event.v1;

    // JDA Permission API
    exports org.mangorage.mangobotcore.api.jda.permission.v1;

    // Command API
    exports org.mangorage.mangobotcore.api.command.v1;
    exports org.mangorage.mangobotcore.api.command.v1.argument;
    exports org.mangorage.mangobotcore.api.command.v1.info;
    exports org.mangorage.mangobotcore.api.command.v1.argument.types;

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
    opens org.mangorage.mangobotcore.internal.plugin.dependency to com.google.gson;


    provides org.mangorage.mangobotcore.api.plugin.v1.Plugin with org.mangorage.mangobotcore.api.plugin.MangoBotCore;
    provides org.mangorage.bootstrap.api.transformer.IClassTransformer with org.mangorage.mangobotcore.internal.transformer.ExampleTransformer;
    provides org.mangorage.bootstrap.api.launch.ILaunchTargetEntrypoint with org.mangorage.mangobotcore.internal.entrypoint.MangoBotEntrypoint;

    uses org.mangorage.mangobotcore.api.plugin.v1.Plugin;
    uses org.mangorage.mangobotcore.api.plugin.v1.IPluginInfoGetter;
    uses org.mangorage.bootstrap.api.transformer.IClassTransformer;
    uses org.mangorage.bootstrap.api.launch.ILaunchTargetEntrypoint;
}
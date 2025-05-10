module org.mangorage.mangobotcore {
    requires  org.jetbrains.annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;

    requires com.google.gson;
    requires net.dv8tion.jda;
    requires org.mangorage.bootstrap;
    requires kotlin.stdlib;

    // Common Utils
    exports org.mangorage.commonutils.misc;
    exports org.mangorage.commonutils.data;
    exports org.mangorage.commonutils.jda;
    exports org.mangorage.commonutils.jda.slash.command;
    exports org.mangorage.commonutils.jda.slash.command.watcher;
    exports org.mangorage.commonutils.log;

    // Plugin API
    exports org.mangorage.mangobotcore.plugin.api;

    // Command API
    exports org.mangorage.mangobotcore.jda.command.api;

    // JDA API
    exports org.mangorage.mangobotcore.jda.event;

    // Config API
    exports org.mangorage.mangobotcore.config.api;

    // Core Plugin
    exports org.mangorage.mangobotcore;



    opens org.mangorage.mangobotcore.plugin.internal;
    opens org.mangorage.mangobotcore.plugin.internal.dependency;
    opens org.mangorage.entrypoint;


    provides org.mangorage.mangobotcore.plugin.api.Plugin with org.mangorage.mangobotcore.MangoBotCore;
    provides org.mangorage.bootstrap.api.transformer.IClassTransformer with org.mangorage.mangobotcore.transformer.ExampleTransformer;

    uses org.mangorage.mangobotcore.plugin.api.Plugin;
    uses org.mangorage.bootstrap.api.transformer.IClassTransformer;
}
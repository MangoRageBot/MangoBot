module org.mangorage.mangobotcore {
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;

    requires com.google.gson;
    requires net.dv8tion.jda;
    requires org.mangorage.scanner;


    requires io.github.classgraph;


    // Common Utils
    exports org.mangorage.commonutils.misc;
    exports org.mangorage.commonutils.data;
    exports org.mangorage.commonutils.config;
    exports org.mangorage.commonutils.jda;
    exports org.mangorage.commonutils.jda.slash.command;
    exports org.mangorage.commonutils.jda.slash.command.watcher;
    exports org.mangorage.commonutils.log;

    // Mangobot Core
    exports org.mangorage.mangobotcore.plugin.api;
    exports org.mangorage.mangobotcore.jda.command.api;
    exports org.mangorage.mangobotcore.jda.event;

    exports org.mangorage.entrypoint;

    exports org.mangorage.mangobotcore.plugin.internal to com.google.gson;
    exports org.mangorage.mangobotcore.plugin.internal.dependency to com.google.gson;

    provides org.mangorage.mangobotcore.plugin.api.Plugin with org.mangorage.mangobotcore.MangoBotCore;
    uses org.mangorage.mangobotcore.plugin.api.Plugin;
}
module org.mangorage.mangobotcore {
    requires com.google.gson;
    requires net.dv8tion.jda;
    requires annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;
    requires org.mangorage.scanner;
    requires jdk.unsupported;

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
    uses org.mangorage.mangobot.loader.Loader;
}
module org.mangorage.mangobot.core {
    requires org.mangorage.scanner;
    requires java.compiler;
    requires org.mangorage.commonutils;
    requires net.dv8tion.jda;
    requires net.minecraftforge.eventbus;

    // API
    exports org.mangorage.mangobotcore.api;
    exports org.mangorage.mangobotcore.plugin.api;
    exports org.mangorage.mangobotcore.jda.command.api;
    exports org.mangorage.mangobotcore.jda.event;
}
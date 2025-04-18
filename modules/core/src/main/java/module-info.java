module org.mangorage.mangobot.core {
    requires org.mangorage.scanner;
    requires java.compiler;
    requires net.dv8tion.jda;

    // API
    exports org.mangorage.mangobotcore.api;
    exports org.mangorage.mangobotcore.plugin.api;
    exports org.mangorage.mangobotcore.jda.command.api;
}
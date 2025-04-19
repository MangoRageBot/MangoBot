module org.mangorage.commonutils {
    exports org.mangorage.commonutils.misc;
    exports org.mangorage.commonutils.data;
    exports org.mangorage.commonutils.config;
    exports org.mangorage.commonutils.jda;
    exports org.mangorage.commonutils.jda.slash.command;
    exports org.mangorage.commonutils.jda.slash.command.watcher;

    requires com.google.gson;
    requires net.dv8tion.jda;
    requires annotations;
    requires org.slf4j;
}
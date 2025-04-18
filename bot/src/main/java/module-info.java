module org.mangorage.mangobot {
    requires org.mangorage.mangobot.core;
    requires net.dv8tion.jda;
    requires org.mangorage.commonutils;

    opens org.mangorage.mangobot to org.mangorage.mangobot.core;
    opens org.mangorage.mangobot.commands to org.mangorage.mangobot.core;
}
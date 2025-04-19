module org.mangorage.mangobot {
    requires org.mangorage.mangobot.core;
    requires net.dv8tion.jda;
    requires org.mangorage.commonutils;
    requires com.google.gson;
    requires luaj.jse;
    requires net.minecraftforge.eventbus;

    opens org.mangorage.mangobot to org.mangorage.mangobot.core;
}
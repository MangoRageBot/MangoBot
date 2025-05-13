open module org.mangorage.mangobotcore {
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires net.minecraftforge.eventbus;

    requires net.dv8tion.jda;
    requires org.mangorage.bootstrap;
    requires kotlin.stdlib;
    requires org.spongepowered.mixin;

    requires com.google.gson;
    requires java.sql;

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


    exports org.mangorage.mixin.core;

    exports org.mangorage.mangobotcore.mixin;


//    opens org.mangorage.mangobotcore.plugin.internal;
//    opens org.mangorage.mangobotcore.plugin.internal.dependency;
//    opens org.mangorage.entrypoint;


    provides org.mangorage.mangobotcore.plugin.api.Plugin with org.mangorage.mangobotcore.MangoBotCore;
    provides org.mangorage.bootstrap.api.transformer.IClassTransformer with org.mangorage.mangobotcore.transformer.ExampleTransformer, org.mangorage.mixin.SpongeMixinTransformer;
    provides org.spongepowered.asm.service.IGlobalPropertyService with org.mangorage.mixin.core.MixinBlackboardImpl;

    uses org.mangorage.mangobotcore.plugin.api.Plugin;
    uses org.mangorage.bootstrap.api.transformer.IClassTransformer;
    uses org.spongepowered.asm.service.IGlobalPropertyService;
}
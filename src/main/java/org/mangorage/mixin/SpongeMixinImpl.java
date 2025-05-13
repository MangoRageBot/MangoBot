package org.mangorage.mixin;

import org.mangorage.mixin.core.MangoBotMixinBootstrap;
import org.mangorage.mixin.core.MixinServiceMangoBot;
import org.mangorage.mixin.transformer.MangoBotTransformer;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.service.MixinService;

import java.lang.reflect.Method;

public final class SpongeMixinImpl {
    private static final SpongeMixinImpl INSTANCE = new SpongeMixinImpl();

    public static void load() {}

    SpongeMixinImpl() {
        // Load
//        System.setProperty("mixin.debug.verbose", "true");
//        System.setProperty("mixin.debug", "true");
//        System.setProperty("mixin.env.disableRefMap", "true");
//        System.setProperty("mixin.checks", "true");

        System.setProperty("mixin.bootstrapService", MangoBotMixinBootstrap.class.getName());
        System.setProperty("mixin.service", MixinServiceMangoBot.class.getName());
        System.setProperty("mixin.env.remapRefMap", "false");

        MixinBootstrap.init();

        Mixins.addConfiguration("mangobotcore.mixins.json");

        completeMixinBootstrap();

        // Mixin Extras Init

        for(Config config : Mixins.getConfigs()) {

                IMixinConfig mixinConfig = config.getConfig();
                mixinConfig.decorate("fabric-modId", "mango");
                mixinConfig.decorate("fabric-compat", 14000);

        }
    }

    private void completeMixinBootstrap() {
        // Move to the default phase.
        try {
            final Method method = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            method.setAccessible(true);
            method.invoke(null, MixinEnvironment.Phase.INIT);
            method.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch(final Exception exception) {
            exception.printStackTrace();
        }

        MangoBotTransformer.getInstance().load();
    }
}



package org.mangorage.mixin.transformer;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;

public final class MangoBotTransformer {
    private static final MangoBotTransformer INSTANCE = new MangoBotTransformer();

    public static MangoBotTransformer getInstance() {
        return INSTANCE;
    }

    private IMixinTransformerFactory factory;
    private IMixinTransformer transformer;

    public void set(IMixinTransformerFactory transformer) {
        this.factory = transformer;
    }

    public IMixinTransformer getTransformer() {
        return transformer;
    }

    public void load() {
        this.transformer = factory.createTransformer();
    }
}

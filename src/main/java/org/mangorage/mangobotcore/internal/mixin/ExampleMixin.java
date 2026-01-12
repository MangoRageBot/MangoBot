package org.mangorage.mangobotcore.internal.mixin;

import org.mangorage.mangobotcore.internal.ExampleThing;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Debug(print = true, export = true)
@Mixin(ExampleThing.class)
public class ExampleMixin {
    public ExampleMixin() {
        System.out.println("LOADED MIXIN!");
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public void load() {
        System.out.println("Loaded via mixins!");
    }
}

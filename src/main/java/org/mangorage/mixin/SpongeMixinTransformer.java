package org.mangorage.mixin;

import org.mangorage.bootstrap.api.transformer.IClassTransformer;
import org.mangorage.bootstrap.api.transformer.TransformResult;
import org.mangorage.bootstrap.api.transformer.TransformerFlag;
import org.mangorage.mixin.transformer.MangoBotTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class SpongeMixinTransformer implements IClassTransformer {

    public SpongeMixinTransformer() {
        SpongeMixinImpl.load();
    }

    public static byte[] getClassBytesFromNode(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    @Override
    public TransformResult transform(String name, byte[] bytes) {
        var transformer = MangoBotTransformer.getInstance().getTransformer();

        if (name.startsWith("org.mangorage.mangobotcore.ExampleThing")) {

            var transformed = transformer.transformClass(
                    MixinEnvironment.getCurrentEnvironment(),
                    name,
                    bytes
            );

            if (!areByteArraysEqual(transformed, bytes)) {
                return new TransformResult(transformed, TransformerFlag.FULL_REWRITE);
            }

        }
        return new TransformResult(null, TransformerFlag.NO_REWRITE);
    }

    public static boolean areByteArraysEqual(byte[] a, byte[] b) {
        if (a == b) return true; // same reference, duh
        if (a == null || b == null) return false; // null? get out
        if (a.length != b.length) return false; // different size = automatic failure

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false; // mismatch? trash it
        }

        return true; // you got lucky, they're equal
    }

    @Override
    public String getName() {
        return "mixin";
    }
}

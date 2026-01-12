package org.mangorage.mangobotcore.internal.transformer;


import org.mangorage.bootstrap.api.transformer.IClassTransformer;
import org.mangorage.bootstrap.api.transformer.TransformResult;
import org.mangorage.bootstrap.api.transformer.TransformerFlag;

public final class ExampleTransformer implements IClassTransformer {
    @Override
    public TransformResult transform(String s, byte[] bytes) {
        return new TransformResult(null, TransformerFlag.NO_REWRITE);
    }

    @Override
    public String getName() {
        return "Test";
    }
}

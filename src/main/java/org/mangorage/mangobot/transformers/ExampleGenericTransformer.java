/*
 * Copyright (c) 2023-2025. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.transformers;

import org.mangorage.mangobot.misc.TypeToken;
import org.mangorage.mangobot.misc.TypeTokenImpl;
import org.mangorage.mangobotapi.core.classloader.ClassFileUtils;
import org.mangorage.mangobotapi.core.classloader.IClassTransformer;
import org.mangorage.mangobotapi.core.classloader.TransformResult;
import org.mangorage.mangobotapi.core.classloader.TransformerFlags;

import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeElement;
import java.lang.classfile.MethodModel;
import java.lang.classfile.attribute.SignatureAttribute;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.concurrent.atomic.AtomicReference;

public class ExampleGenericTransformer implements IClassTransformer {

    private static final String EXAMPLE_CLASS = "org/mangorage/mangobot/misc/Example";
    private static final String GET_TYPE_TOKEN_METHOD = "getTypeToken";

    @Override
    public TransformResult transform(byte[] preData) {
        var file = ClassFile.of();
        var classModel = file.parse(preData);
        byte[] data = null;

        if (classModel.thisClass().name().stringValue().equals(EXAMPLE_CLASS)) {
            data = file.build(classModel.thisClass().asSymbol(), cb -> {
                classModel.elementList().forEach(e -> {
                    if (e instanceof MethodModel me) {
                        if (!me.methodName().stringValue().equals(GET_TYPE_TOKEN_METHOD)) {
                            cb.with(e);
                        } else {
                            cb.withMethod(me.methodName(), me.methodType(),
                                    ClassFileUtils.getMethodFlagsInt(AccessFlag.PUBLIC),
                                    a -> a.withCode(cbb -> {
                                        for (CodeElement element : me.code().get().elementList()) {
                                            cbb.with(element);
                                        }
                                    }));
                        }
                    } else {
                        cb.with(e);
                    }
                });
            });
        } else if (classModel.superclass().get().name().stringValue().startsWith(EXAMPLE_CLASS)) {
            AtomicReference<String> type = new AtomicReference<>();
            data = file.build(classModel.thisClass().asSymbol(), cb -> {

                classModel.elementList().forEach(e -> {
                    if (e instanceof MethodModel me) {
                        if (!me.methodName().stringValue().equals(GET_TYPE_TOKEN_METHOD)) {
                            cb.with(e);
                        }
                    } else {
                        cb.with(e);
                    }
                    if (e instanceof SignatureAttribute s) {
                        type.set(ClassFileUtils.parseSignature(s.signature().stringValue()));
                    }
                });

                cb.withMethodBody(GET_TYPE_TOKEN_METHOD,
                        ClassFileUtils.getMethodDesc(TypeToken.class),
                        ClassFileUtils.getMethodFlagsInt(AccessFlag.PUBLIC, AccessFlag.FINAL),
                        mcb -> mcb.aload(0)
                                .new_(ClassFileUtils.getClassName(TypeTokenImpl.class))
                                .dup()
                                .ldc(type.get())
                                .invokespecial(ClassFileUtils.getClassName(TypeTokenImpl.class),
                                        "<init>",
                                        MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)V"))
                                .areturn());
            });
        }

        return (data != null)
                ? TransformerFlags.FULL_REWRITE.of(data)
                : TransformerFlags.NO_REWRITE.of(preData);
    }

    @Override
    public String getName() {
        return "Example Generic Transformer";
    }
}

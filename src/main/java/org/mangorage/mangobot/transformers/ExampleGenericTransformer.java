/*
 * Copyright (c) 2023. MangoRage
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

import org.mangorage.mangobotapi.core.classloader.IClassTransformer;
import org.mangorage.mangobotapi.core.classloader.TransformerFlags;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;

public class ExampleGenericTransformer implements IClassTransformer {
    private static final String CLASS_TYPE = "org/mangorage/mangobot/misc/ExampleGeneric";
    private static final String CLONED_CLASS_TYPE = "org/mangorage/mangobot/misc/ExampleGeneric$ClonedExampleGeneric";
    private static final String TYPE_TOKEN = "org/mangorage/mangobot/misc/TypeToken";
    private static final String FUNC_NAME = "getType";
    private static final String FUNC_DESC = "()Lorg/mangorage/mangobot/misc/TypeToken;";


    @Override
    public TransformerFlags transform(ClassNode classNode, Type classType) {
        if (CLONED_CLASS_TYPE.equals(classNode.name)) return TransformerFlags.NO_REWRITE;

        if (CLASS_TYPE.equals(classNode.name)) {
            for (MethodNode mtd : classNode.methods) {
                if (FUNC_NAME.equals(mtd.name) && FUNC_DESC.equals(mtd.desc)) {
                    mtd.access &= ~Opcodes.ACC_FINAL;
                }
            }
            return TransformerFlags.SIMPLE_REWRITE;
        } else if (CLASS_TYPE.equals(classNode.superName)) {
            AtomicReference<String> cls = new AtomicReference<>();
            System.out.println(classNode.signature);

            SignatureReader reader = new SignatureReader(classNode.signature);
            reader.accept(new SignatureVisitor(Opcodes.ASM9) {
                final Deque<String> stack = new ArrayDeque<>();

                @Override
                public void visitClassType(final String name) {
                    stack.push(name);
                }

                @Override
                public void visitInnerClassType(final String name) {
                    stack.push(stack.pop() + '$' + name);
                }

                @Override
                public void visitEnd() {
                    var val = stack.pop();
                    if (!stack.isEmpty() && CLASS_TYPE.equals(stack.peek()))
                        cls.set(val);
                }
            });

            if (cls.get() == null)
                throw new IllegalStateException("Could not find signature for GenericExample on " + classNode.name + " from " + classNode.signature);

            var mtd = classNode.visitMethod(Opcodes.ACC_PUBLIC, FUNC_NAME, FUNC_DESC, null, new String[0]);

            // Create and return a TypeToken
            mtd.visitTypeInsn(Opcodes.NEW, TYPE_TOKEN);
            mtd.visitInsn(Opcodes.DUP);
            mtd.visitLdcInsn(cls.get());  // Load the class name
            mtd.visitMethodInsn(Opcodes.INVOKESPECIAL, TYPE_TOKEN, "<init>", "(Ljava/lang/String;)V", false);
            mtd.visitInsn(Opcodes.ARETURN);
            mtd.visitEnd();

            return TransformerFlags.FULL_REWRITE;
        }

        return TransformerFlags.NO_REWRITE;
    }

    @Override
    public String getName() {
        return "ExampleGenericTransformer";
    }
}
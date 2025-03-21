/*
 * Copyright (c) 2024-2025. MangoRage
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

package org.mangorage.mangobotapi.core.classloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.Arrays;

public class ClassFileUtils {
    public static void writeBytesToFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }

    public static String parseSignature(String signature) {
        int start = signature.indexOf('<');
        if (start == -1) {
            return null;
        }

        int end = signature.indexOf('>');
        if (end == -1) {
            return null;
        }

        return signature.substring(start + 1, end - 1);
    }

    public static ClassDesc getClassName(Class<?> clazz) {
        return ClassDesc.ofDescriptor(clazz.descriptorString());
    }

    public static MethodTypeDesc getMethodDesc(Class<?> returnType, Class<?>... args) {
        var paramList = Arrays.stream(args)
                .map(ClassFileUtils::getClassName)
                .toList();
        if (paramList.isEmpty()) {
            return MethodTypeDesc.of(
                    getClassName(returnType)
            );
        } else {
            return MethodTypeDesc.of(
                    getClassName(returnType),
                    paramList
            );
        }
    }

    public static int getMethodFlagsInt(AccessFlag... flags) {
        int mask = 0;
        for (AccessFlag flag : flags) {
            mask |= flag.mask();
        }
        return mask;
    }
}

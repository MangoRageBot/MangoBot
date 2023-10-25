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

package org.mangorage.mangobotapi.core.registry.permissions;

import org.mangorage.mangobotapi.core.reflections.ReflectionsUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;


/**
 * Permissions here can be seen and managed by guilds...
 */
public class PermissionRegistry {
    private static HashMap<String, BasicPermission> PERMISSIONS = new HashMap<>();


    public static void load() {
        ReflectionsUtils.REFLECTIONS.getTypesAnnotatedWith(BasicPermission.AutoRegister.class).forEach(cls -> {
            for (Field field : cls.getDeclaredFields()) {

                if (!Modifier.isStatic(field.getModifiers())) return;
                var permAnnotation = field.getAnnotation(BasicPermission.Register.class);
                if (permAnnotation == null) return;
                if (field.getType() != BasicPermission.class) return;
                try {
                    var obj = field.get(PermissionRegistry.class);
                    if (obj instanceof BasicPermission permission) PermissionRegistry.register(permission);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static BasicPermission getPermission(String id) {
        return PERMISSIONS.get(id);
    }

    public static List<String> getPermissions() {
        return List.copyOf(PERMISSIONS.keySet());
    }

    public static void register(BasicPermission permission) {
        PERMISSIONS.put(permission.getId(), permission);
    }
}

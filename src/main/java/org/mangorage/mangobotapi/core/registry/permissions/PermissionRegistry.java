/*
 * Copyright (c) 2023-2024. MangoRage
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

import org.mangorage.mangobotapi.core.data.DataHandler;
import org.mangorage.mangobotapi.core.plugin.api.CorePlugin;

import java.util.HashMap;
import java.util.List;


/**
 * Permissions here can be seen and managed by guilds...
 */
public class PermissionRegistry {
    private final CorePlugin plugin;
    private final HashMap<String, BasicPermission> PERMISSIONS = new HashMap<>();

    private final DataHandler<BasicPermission> PERMISSION_DATA_HANDLER = DataHandler.create()
            .path("data/permissions")
            .build(BasicPermission.class);

    public PermissionRegistry(CorePlugin plugin) {
        this.plugin = plugin;
        PERMISSION_DATA_HANDLER.load(plugin.getPluginDirectory()).forEach(bp -> {
            PERMISSIONS.put(bp.getId(), bp);
        });
    }

    public BasicPermission getPermission(String id) {
        return PERMISSIONS.get(id);
    }

    public List<String> getPermissions() {
        return List.copyOf(PERMISSIONS.keySet());
    }

    public void register(BasicPermission permission) {
        PERMISSIONS.put(permission.getId(), permission);
    }

    public void save() {
        for (BasicPermission permission : PERMISSIONS.values())
            save(permission);
    }

    public void save(BasicPermission permission) {
        PERMISSION_DATA_HANDLER.save(plugin.getPluginDirectory(), permission);
    }
}

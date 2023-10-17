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

package org.mangorage.mangobot.core.config;

import net.dv8tion.jda.api.Permission;
import org.mangorage.mangobotapi.core.registry.BasicPermission;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;

public class BotPermissions {

    /**
     * // Admin Role
     * PERMISSIONS.register(BotPermissions.TRICK_ADMIN, UserPermission.of("1129067881842360381"));
     * // Moderators Role
     * PERMISSIONS.register(BotPermissions.TRICK_ADMIN, UserPermission.of("1129070272302022656"));
     * // Mango Bot Tester Role
     * PERMISSIONS.register(BotPermissions.TRICK_ADMIN, UserPermission.of("1150880910745538631"));
     **/

    public static final BasicPermission PLAYING = BasicPermission.create("playing");
    public static final BasicPermission TRICK_ADMIN = BasicPermission.create("trickadmin");
    public static final BasicPermission PREFIX_ADMIN = BasicPermission.create("prefix");
    public static final BasicPermission MOD_MAIL = BasicPermission.create("mod_mail");
    public static final BasicPermission PERMISSION_ADMIN = BasicPermission.create("permission_admin");


    static {
        PermissionRegistry.register(PLAYING);
        PermissionRegistry.register(TRICK_ADMIN);
        PermissionRegistry.register(PREFIX_ADMIN);
        PermissionRegistry.register(MOD_MAIL);
        PermissionRegistry.register(PERMISSION_ADMIN);


        PLAYING.addPermission(Permission.ADMINISTRATOR);
        TRICK_ADMIN.addPermission(Permission.ADMINISTRATOR);
        PREFIX_ADMIN.addPermission(Permission.ADMINISTRATOR);
        MOD_MAIL.addPermission(Permission.ADMINISTRATOR);
        PERMISSION_ADMIN.addPermission(Permission.ADMINISTRATOR);

        TRICK_ADMIN.addRole("1129059589325852724", "1129067881842360381");
        TRICK_ADMIN.addRole("1129059589325852724", "1129070272302022656");
        TRICK_ADMIN.addRole("1129059589325852724", "1150880910745538631");
    }

    public static void init() {
    }
}

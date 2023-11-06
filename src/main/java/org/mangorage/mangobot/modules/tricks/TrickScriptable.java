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

package org.mangorage.mangobot.modules.tricks;

import net.dv8tion.jda.api.entities.Message;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class TrickScriptable {
    private final Message message;

    public TrickScriptable(Message message) {
        this.message = message;
    }

    public void reply(String input) {
        message.reply(input).queue();
    }


    public static void execute(String script, Message message, String[] args) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue myClassInstance = CoerceJavaToLua.coerce(new TrickScriptable(message));
        globals.set("JDA", myClassInstance);
        LuaValue code = globals.load(script);
        code.call();

        LuaValue method = globals.get("execute");
        LuaTable arr = new LuaTable();
        for (int i = 0; i < args.length; i++) {
            arr.set(i + 1, LuaValue.valueOf(args[i]));
        }
        method.call(arr);
    }
}

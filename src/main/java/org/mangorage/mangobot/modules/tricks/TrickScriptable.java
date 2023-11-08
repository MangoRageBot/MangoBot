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
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.mangorage.mangobot.modules.tricks.lua.JDALib;
import org.mangorage.mangobot.modules.tricks.lua.JDAMessageLib;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TrickScriptable {
    public static Globals sandBoxedGlobals(Message message) {
        Globals server_globals = new Globals();

        server_globals.load(new JseBaseLib());
        server_globals.load(new PackageLib());
        server_globals.load(new JseMathLib());

        LuaValue sandbox = new LuaTable();
        sandbox.set("JDALib", CoerceJavaToLua.coerce(new JDALib(message.getJDA())));
        sandbox.set("JDAMessage", CoerceJavaToLua.coerce(new JDAMessageLib(message)));


        LoadState.install(server_globals);
        LuaC.install(server_globals);

        var array = new String[]{"os", "io", "luajava", "debug", "load", "loadfile"};

        Consumer<String> NILLIFY = a -> {
            server_globals.set(a, LuaValue.NIL);
        };

        for (String library : array)
            NILLIFY.accept(library);

        server_globals.set("api", sandbox);

        return server_globals;
    }

    public static void execute(String script, Message message, String[] args) {
        // Create a ScheduledExecutorService
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        AtomicReference<ScheduledFuture<?>> TASK = new AtomicReference<>();

        // Define the task you want to execute

        Globals globals = sandBoxedGlobals(message);
        LuaValue code = globals.load(script);

        Runnable task = () -> {
            try {
                code.call();

                LuaValue method = globals.get("execute");
                LuaTable arr = new LuaTable();
                for (int i = 0; i < args.length; i++)
                    arr.set(i + 1, LuaValue.valueOf(args[i]));

                if (!method.isnil()) {
                    method.call(arr);
                }
                if (TASK.get() != null) {
                    var a = TASK.get();
                    if (a.isDone() || a.isCancelled()) return;
                    a.cancel(true);
                }
            } catch (Exception e) {
                message.reply(e.getMessage()).mentionRepliedUser(false).queue();
            }
        };

        // Submit the task to the executor
        Future<?> future = executor.submit(() -> {
            long time = System.currentTimeMillis();
            task.run();
            System.out.println("Script Took: %s ms".formatted(System.currentTimeMillis() - time));
        });

        // Schedule a task to cancel the original task if it runs longer than the timeout
        TASK.set(executor.schedule(() -> {
            if (future.isDone() || future.isCancelled()) return;
            message.reply("Trick took to long to run. Stopping Trick...").mentionRepliedUser(false).queue();
            future.cancel(true);
        }, 25, TimeUnit.MILLISECONDS));

        // Shutdown the executor
        executor.shutdown();
    }
}
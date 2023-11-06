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

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TrickScriptable {
    private final Message message;

    public TrickScriptable(Message message) {
        this.message = message;
    }

    public void reply(String input) {
        message.reply(input).mentionRepliedUser(false).queue();
    }

    public static Globals sandBoxedGlobals(Message message) {
        Globals server_globals = new Globals();

        // Load only the essential libraries you want to make available in the sandbox
        server_globals.load(new JseBaseLib());
        server_globals.load(new PackageLib());
        server_globals.load(new JseMathLib());

        // Create a new environment for the sandbox
        LuaValue sandbox = new LuaTable();
        sandbox.set("print", server_globals.get("print")); // Allow the 'print' function

        // Create the sandboxed environment with the custom functions
        LuaValue JDA = CoerceJavaToLua.coerce(new TrickScriptable(message));
        sandbox.set("JDA", JDA);
        // Set any other custom variables or functions that your Lua script expects in the sandbox

        // Load the Lua standard libraries into the sandbox
        LoadState.install(server_globals);
        LuaC.install(server_globals);

        // Remove dangerous libraries
        server_globals.set("os", LuaValue.NIL);
        server_globals.set("io", LuaValue.NIL);
        server_globals.set("luajava", LuaValue.NIL);
        server_globals.set("debug", LuaValue.NIL);
        server_globals.set("api", sandbox);

        return server_globals;
    }

    public static void execute(String script, Message message, String[] args) {
        // Create a ScheduledExecutorService
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        AtomicReference<ScheduledFuture<?>> TASK = new AtomicReference<>();
        // Define the task you want to execute
        Runnable task = () -> {
            try {
                Globals globals = sandBoxedGlobals(message);
                LuaValue code = globals.load(script);
                code.call();

                LuaValue method = globals.get("execute");
                LuaTable arr = new LuaTable();
                for (int i = 0; i < args.length; i++)
                    arr.set(i + 1, LuaValue.valueOf(args[i]));

                method.call(arr);
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
        Future<?> future = executor.submit(task);

        // Set a timeout for the task (in this example, 5 seconds)
        int timeoutInSeconds = 5;

        // Schedule a task to cancel the original task if it runs longer than the timeout
        TASK.set(executor.schedule(() -> {
            if (future.isDone() || future.isCancelled()) return;
            message.reply("Trick took to long to run. Stopping Trick...").mentionRepliedUser(false).queue();
            future.cancel(true);
        }, timeoutInSeconds, TimeUnit.SECONDS));

        // Shutdown the executor
        executor.shutdown();
    }
}
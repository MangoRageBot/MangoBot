package org.mangorage.mangobot.commands.trick;


import org.mangorage.mangobot.commands.trick.lua.MemoryBank;

import java.util.HashMap;

public final class EmptyTrick extends Trick {
    public static final EmptyTrick INSTANCE = new EmptyTrick();

    private EmptyTrick() {
        super(null, 0);
    }

    @Override
    public MemoryBank getMemoryBank() {
        return new MemoryBank(new HashMap<>());
    }
}

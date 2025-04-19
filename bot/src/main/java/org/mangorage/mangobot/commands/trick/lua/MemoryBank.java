package org.mangorage.mangobot.commands.trick.lua;

import java.util.HashMap;
import java.util.Map;

public final class MemoryBank  {
    private HashMap<String, Object> bank;

    public MemoryBank(HashMap<String, Object> bank) {
        this.bank = bank;
    }

    public Map<String, Object> bank() {
        return bank;
    }

}

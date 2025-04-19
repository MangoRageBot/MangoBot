package org.mangorage.mangobot.commands.trick.lua.objects;

public class LuaStringArray {
    private final String[] strings;

    public LuaStringArray(String[] strings) {
        this.strings = strings;
    }

    public int getSize() {
        return strings.length;
    }

    public String getEntry(int index) {
        return strings[index];
    }

    public boolean isEmpty() {
        if (strings.length == 0) return true;
        return strings.length == 1 && strings[0].isEmpty();
    }
}

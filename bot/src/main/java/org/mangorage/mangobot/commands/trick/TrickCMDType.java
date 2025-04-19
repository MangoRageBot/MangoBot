package org.mangorage.mangobot.commands.trick;

public enum TrickCMDType {
    ADD("-a", "-add"),
    REMOVE("-r", "-remove"),
    MODIFY("-m", "-e", "-modify", "-edit"),
    SHOW("-s", "-show"),
    INFO("-i", "-info"),
    LIST("-l", "-list"),
    TRANSFER("-t", "-transfer"),
    LOCK("-lock"),
    FIND("-f", "-find"),
    NONE();

    private String[] strings;

    TrickCMDType(String... strings) {
        this.strings = strings;
    }

    public static TrickCMDType getType(String s) {
        for (var type : TrickCMDType.values())
            for (var str : type.getStrings())
                if (s.equals(str))
                    return type;

        return NONE;
    }

    private String[] getStrings() {
        return strings;
    }
}

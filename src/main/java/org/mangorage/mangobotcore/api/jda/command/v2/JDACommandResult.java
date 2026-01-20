package org.mangorage.mangobotcore.api.jda.command.v2;

public enum JDACommandResult {
    PASS(null),
    ERROR(null),
    INVALID_COMMAND("Invalid Command"),

    NO_PERMISSION("You do not have permission to execute this command."),
    GUILD_ONLY("This command can only be used in a guild."),
    DM_ONLY("This command can only be used in DMs."),

    UNDER_MAINTENANCE("This command is currently under maintenance.");

    private final String message;

    JDACommandResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

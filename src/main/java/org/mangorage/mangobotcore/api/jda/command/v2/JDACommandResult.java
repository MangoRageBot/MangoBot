package org.mangorage.mangobotcore.api.jda.command.v2;

public enum JDACommandResult {
    PASS(null),
    FAIL("Command execution failed."),
    ERROR("An error occurred while executing the command."),
    UNDER_MAINTENANCE("This command is currently under maintenance."),
    NO_PERMISSION("You do not have permission to execute this command."),
    INVALID_COMMAND("Invalid command."),
    GUILD_ONLY("This command can only be used in a guild."),;

    private final String message;

    JDACommandResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

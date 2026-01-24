package org.mangorage.mangobotcore.api.command.v1.info;

public final class ParameterPart {
    private final String name;
    private final String type;
    private final String argumentType;
    private final String description;
    private final String suggestions;
    private final boolean required;

    public ParameterPart(String name, String type, String argumentType, String description, String suggestions, boolean required) {
        this.name = name;
        this.type = type;
        this.argumentType = argumentType;
        this.description = description;
        this.suggestions = suggestions;
        this.required = required;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getArgumentType() { return argumentType; }
    public String getDescription() { return description; }
    public String getSuggestions() { return suggestions; }
    public boolean isRequired() { return required; }
}
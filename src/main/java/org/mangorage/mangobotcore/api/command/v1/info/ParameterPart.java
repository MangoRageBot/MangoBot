package org.mangorage.mangobotcore.api.command.v1.info;

public class ParameterPart {
    private final String name;
    private final String type;
    private final String suggestions;
    private final boolean required;

    public ParameterPart(String name, String type, String suggestions, boolean required) {
        this.name = name;
        this.type = type;
        this.suggestions = suggestions;
        this.required = required;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getSuggestions() { return suggestions; }
    public boolean isRequired() { return required; }
}
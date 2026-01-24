package org.mangorage.mangobotcore.api.command.v1.info;

import org.mangorage.mangobotcore.api.command.v1.CommandInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record CommandData(List<CommandPart> commandParts) {
    public CommandInfo buildCommandInfo(boolean advanced) {
        List<String> usages = new ArrayList<>();
        Map<String, List<String>> extraInfo = new LinkedHashMap<>();

        for (CommandPart part : commandParts()) {
            StringBuilder usage = new StringBuilder(part.getName());

            for (ParameterPart param : part.getParameters()) {
                String renderedName = param.getName();

                if (advanced) {
                    renderedName += ":" + param.getType();
                }

                if (param.isRequired()) {
                    usage.append(" <").append(renderedName).append(">");
                } else {
                    usage.append(" [").append(renderedName).append("]");
                }

                // build extra info per parameter
                List<String> info = new ArrayList<>();
                info.add("Description: " + param.getName()); // or store real description if you add it
                info.add("Type: " + param.getType());
                info.add("Suggestions: " + param.getSuggestions());

                extraInfo.put(param.getName(), info);
            }

            usages.add(usage.toString());
        }

        return new CommandInfo(usages, extraInfo);
    }
}

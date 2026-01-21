package org.mangorage.mangobotcore.api.command.v1;

import java.util.List;
import java.util.Map;

public record CommandInfo(
        List<String> usages,
        Map<String, List<String>> extraInfo
) { }

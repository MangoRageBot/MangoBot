package org.mangorage.mangobotcore.plugin.internal;

import com.google.gson.annotations.Expose;
import org.mangorage.mangobotcore.plugin.api.IExtraMap;

import java.util.Map;

public record ExtraMap(
        @Expose
        Map<String, Object> data
) implements IExtraMap {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getKey(String s, Class<T> type) {
        return (T) data.get(s);
    }
}

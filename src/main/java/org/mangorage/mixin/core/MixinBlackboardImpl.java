package org.mangorage.mixin.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the mixin blackboard provider.
 *
 * @author vectrix
 * @since 1.0.0
 */
public final class MixinBlackboardImpl implements IGlobalPropertyService {
    private final Map<IPropertyKey, Object> map = new HashMap<>();

    public MixinBlackboardImpl() {
    }

    public record Key(String name) implements IPropertyKey {}

    @Override
    public IPropertyKey resolveKey(final @NotNull String name) {
        return new Key(name);
    }

    @Override
    public <T> T getProperty(final @NotNull IPropertyKey key) {
        return this.getProperty(key, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperty(final @NotNull IPropertyKey key, final @NotNull Object other) {
        map.put(key, other);
    }

    @Override
    public @Nullable String getPropertyString(final @NotNull IPropertyKey key, final @Nullable String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getProperty(final @NotNull IPropertyKey key, final @Nullable T defaultValue) {
        return (T) map.getOrDefault(key, defaultValue);
    }

}

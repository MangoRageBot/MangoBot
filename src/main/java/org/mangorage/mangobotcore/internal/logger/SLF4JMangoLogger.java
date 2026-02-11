package org.mangorage.mangobotcore.internal.logger;

import org.mangorage.bootstrap.api.logging.IMangoLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class SLF4JMangoLogger implements IMangoLogger {

    private final Logger logger;

    public SLF4JMangoLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public SLF4JMangoLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    public SLF4JMangoLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws UnsupportedOperationException {
        if (aClass.isInstance(logger)) {
            return aClass.cast(logger);
        }
        throw new UnsupportedOperationException("Cannot unwrap to " + aClass.getName());
    }

    // ------------------------------------------------
    // Standard Logging
    // ------------------------------------------------

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object... objects) {
        logger.trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(s, throwable);
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void debug(String s, Object... objects) {
        logger.debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        logger.debug(s, throwable);
    }

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void info(String s, Object... objects) {
        logger.info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        logger.info(s, throwable);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public void warn(String s, Object... objects) {
        logger.warn(s, objects);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        logger.warn(s, throwable);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void error(String s, Object... objects) {
        logger.error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    // ------------------------------------------------
    // Mango Custom Methods
    // ------------------------------------------------

    @Override
    public void rainbow(String s) {
        logger.info("🌈 {}", s);
    }

    @Override
    public void celebration(String s) {
        logger.info("🎉 {}", s);
    }

    @Override
    public void dramatic(String s) {
        logger.warn("🔥 DRAMATIC: {}", s);
    }

    @Override
    public void whisper(String s) {
        logger.debug("🤫 {}", s);
    }

    @Override
    public void shout(String s) {
        logger.warn("📢 {}", s.toUpperCase());
    }

    @Override
    public void withEmoji(String emoji, String s) {
        logger.info("{} {}", emoji, s);
    }

    @Override
    public void withBorder(String s) {
        String border = "=".repeat(Math.max(10, s.length() + 4));
        logger.info("\n{}\n| {} |\n{}", border, s, border);
    }

    @Override
    public void withContext(String context, String s) {
        logger.info("[{}] {}", context, s);
    }

    // ------------------------------------------------
    // Level Checks
    // ------------------------------------------------

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public String getName() {
        return logger.getName();
    }
}

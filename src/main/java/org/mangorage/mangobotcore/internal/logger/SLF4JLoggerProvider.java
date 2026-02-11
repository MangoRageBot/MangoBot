package org.mangorage.mangobotcore.internal.logger;

import org.mangorage.bootstrap.api.logging.AbstractLoggerProvider;
import org.mangorage.bootstrap.api.logging.IMangoLogger;

public final class SLF4JLoggerProvider extends AbstractLoggerProvider {

    public SLF4JLoggerProvider() {
        super("slf4j");
    }

    @Override
    protected IMangoLogger createLogger(String s) {
        return new SLF4JMangoLogger(s);
    }
}

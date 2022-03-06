package me.koply.kcommando.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Kogger {

    private static final Logger LOGGER;
    static {
        LOGGER = Logger.getLogger("KCommando");
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            public String format(LogRecord record) {
                return String.format("[%s %s] %s -> %s\n", formatter.format(new Date(record.getMillis())), record.getLevel(), record.getLoggerName(), record.getMessage());
            }
        });
        LOGGER.addHandler(consoleHandler);
    }

    public static void info(String text) {
        LOGGER.info(text);
    }

    public static void warn(String text) {
        LOGGER.warning(text);
    }

}
package com.reynaud.wonders.util;

import com.reynaud.wonders.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to use logging service more easily across the application
 * Inject LoggingUtil into your services and use its convenient methods
 */
@Component
public class LoggingUtil {

    private static LoggingService loggingService;

    @Autowired
    public LoggingUtil(LoggingService loggingService) {
        LoggingUtil.loggingService = loggingService;
    }

    /**
     * Static helper methods for convenient logging
     */
    public static void debug(String message, String source) {
        if (loggingService != null) {
            loggingService.debug(message, source);
        }
    }

    public static void info(String message, String source) {
        if (loggingService != null) {
            loggingService.info(message, source);
        }
    }

    public static void warning(String message, String source) {
        if (loggingService != null) {
            loggingService.warning(message, source);
        }
    }

    public static void error(String message, String source) {
        if (loggingService != null) {
            loggingService.error(message, source);
        }
    }

    public static void error(String message, String source, Throwable throwable) {
        if (loggingService != null) {
            loggingService.error(message, source, throwable);
        }
    }

    public static void critical(String message, String source) {
        if (loggingService != null) {
            loggingService.critical(message, source);
        }
    }

    public static void critical(String message, String source, Throwable throwable) {
        if (loggingService != null) {
            loggingService.critical(message, source, throwable);
        }
    }
}

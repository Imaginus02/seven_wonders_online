package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.LogDAO;
import com.reynaud.wonders.entity.LogEntry;
import com.reynaud.wonders.model.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service for managing application logs.
 * Provides methods for logging at different levels and retrieving logs.
 */
@Service
public class LoggingService {

    private final LogDAO logDAO;
    private static final int DEFAULT_PAGE_SIZE = 50;

    public LoggingService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    /**
     * Log a debug message
     */
    @Transactional
    public void debug(String message, String source) {
        log(LogLevel.DEBUG, message, source, null);
    }

    /**
     * Log an info message
     */
    @Transactional
    public void info(String message, String source) {
        log(LogLevel.INFO, message, source, null);
    }

    /**
     * Log a warning message
     */
    @Transactional
    public void warning(String message, String source) {
        log(LogLevel.WARNING, message, source, null);
    }

    /**
     * Log an error message
     */
    @Transactional
    public void error(String message, String source) {
        log(LogLevel.ERROR, message, source, null);
    }

    /**
     * Log an error message with exception
     */
    @Transactional
    public void error(String message, String source, Throwable throwable) {
        log(LogLevel.ERROR, message, source, throwable);
    }

    /**
     * Log a critical message
     */
    @Transactional
    public void critical(String message, String source) {
        log(LogLevel.CRITICAL, message, source, null);
    }

    /**
     * Log a critical message with exception
     */
    @Transactional
    public void critical(String message, String source, Throwable throwable) {
        log(LogLevel.CRITICAL, message, source, throwable);
    }

    /**
     * Internal method to create and save log entries
     */
    private void log(LogLevel level, String message, String source, Throwable throwable) {
        LogEntry logEntry = new LogEntry(level, message, source);
        
        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            logEntry.setStackTrace(sw.toString());
        }
        
        logDAO.save(logEntry);
    }

    /**
     * Get all logs with pagination
     */
    @Transactional(readOnly = true)
    public Page<LogEntry> getAllLogs(int page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_PAGE_SIZE);
        return logDAO.findAllByOrderByTimestampDesc(pageable);
    }

    /**
     * Get logs by level
     */
    @Transactional(readOnly = true)
    public Page<LogEntry> getLogsByLevel(LogLevel level, int page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_PAGE_SIZE);
        List<LogLevel> levels = Arrays.asList(level);
        return logDAO.findByLogLevelIn(levels, pageable);
    }

    /**
     * Get logs by source
     */
    @Transactional(readOnly = true)
    public Page<LogEntry> getLogsBySource(String source, int page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_PAGE_SIZE);
        return logDAO.findBySourceOrderByTimestampDesc(source, pageable);
    }

    /**
     * Get unread logs
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getUnreadLogs() {
        return logDAO.findByReadByAdminFalseOrderByTimestampDesc();
    }

    /**
     * Get count of unread logs
     */
    @Transactional(readOnly = true)
    public long getUnreadLogsCount() {
        return logDAO.countByReadByAdminFalse();
    }

    /**
     * Get recent logs (since specified time)
     */
    @Transactional(readOnly = true)
    public List<LogEntry> getRecentLogs(LocalDateTime since) {
        return logDAO.findByTimestampAfterOrderByTimestampDesc(since);
    }

    /**
     * Mark specific logs as read
     */
    @Transactional
    public void markAsRead(List<Long> logIds) {
        for (Long id : logIds) {
            logDAO.findById(id).ifPresent(log -> {
                log.setReadByAdmin(true);
                logDAO.save(log);
            });
        }
    }

    /**
     * Mark all logs as read
     */
    @Transactional
    public void markAllAsRead() {
        List<LogEntry> unreadLogs = getUnreadLogs();
        for (LogEntry log : unreadLogs) {
            log.setReadByAdmin(true);
            logDAO.save(log);
        }
    }

    /**
     * Clear old logs (older than specified days)
     */
    @Transactional
    public long clearOldLogs(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<LogEntry> oldLogs = logDAO.findByTimestampBeforeOrderByTimestampDesc(cutoffDate);
        long count = oldLogs.size();
        logDAO.deleteAll(oldLogs);
        return count;
    }
}

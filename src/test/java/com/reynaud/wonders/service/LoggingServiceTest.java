package com.reynaud.wonders.service;

import com.reynaud.wonders.dao.LogDAO;
import com.reynaud.wonders.entity.LogEntry;
import com.reynaud.wonders.model.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("LoggingService Tests")
class LoggingServiceTest {

    @Mock
    private LogDAO logDAO;

    @InjectMocks
    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should log info message")
    void testLogInfo() {
        String message = "Test info message";
        String source = "TestSource";

        loggingService.info(message, source);

        verify(logDAO, times(1)).save(any(LogEntry.class));
    }

    @Test
    @DisplayName("Should log error with throwable")
    void testLogErrorWithThrowable() {
        String message = "Test error";
        String source = "TestSource";
        Exception exception = new RuntimeException("Test exception");

        loggingService.error(message, source, exception);

        verify(logDAO, times(1)).save(any(LogEntry.class));
    }

    @Test
    @DisplayName("Should retrieve all logs with pagination")
    void testGetAllLogs() {
        LogEntry log1 = new LogEntry(LogLevel.INFO, "Message 1", "Source1");
        LogEntry log2 = new LogEntry(LogLevel.ERROR, "Message 2", "Source2");
        Page<LogEntry> mockPage = new PageImpl<>(Arrays.asList(log1, log2), PageRequest.of(0, 50), 2);

        when(logDAO.findAllByOrderByTimestampDesc(any())).thenReturn(mockPage);

        Page<LogEntry> result = loggingService.getAllLogs(1);

        assertEquals(2, result.getTotalElements());
        verify(logDAO, times(1)).findAllByOrderByTimestampDesc(any());
    }

    @Test
    @DisplayName("Should retrieve logs by level")
    void testGetLogsByLevel() {
        LogEntry errorLog = new LogEntry(LogLevel.ERROR, "Error message", "Source");
        Page<LogEntry> mockPage = new PageImpl<>(Arrays.asList(errorLog), PageRequest.of(0, 50), 1);

        when(logDAO.findByLogLevelIn(any(), any())).thenReturn(mockPage);

        Page<LogEntry> result = loggingService.getLogsByLevel(LogLevel.ERROR, 1);

        assertEquals(1, result.getTotalElements());
        verify(logDAO, times(1)).findByLogLevelIn(any(), any());
    }

    @Test
    @DisplayName("Should retrieve unread logs")
    void testGetUnreadLogs() {
        LogEntry unreadLog = new LogEntry(LogLevel.WARNING, "Unread message", "Source");
        unreadLog.setReadByAdmin(false);

        when(logDAO.findByReadByAdminFalseOrderByTimestampDesc())
                .thenReturn(Arrays.asList(unreadLog));

        List<LogEntry> result = loggingService.getUnreadLogs();

        assertEquals(1, result.size());
        assertFalse(result.get(0).getReadByAdmin());
    }

    @Test
    @DisplayName("Should count unread logs")
    void testGetUnreadLogsCount() {
        when(logDAO.countByReadByAdminFalse()).thenReturn(5L);

        long count = loggingService.getUnreadLogsCount();

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Should mark logs as read")
    void testMarkAsRead() {
        LogEntry log = new LogEntry(LogLevel.INFO, "Test", "Source");
        log.setId(1L);

        when(logDAO.findById(1L)).thenReturn(java.util.Optional.of(log));

        loggingService.markAsRead(Arrays.asList(1L));

        assertTrue(log.getReadByAdmin());
        verify(logDAO, times(1)).save(log);
    }

    @Test
    @DisplayName("Should verify different log levels have correct severity")
    void testLogLevelSeverity() {
        assertTrue(LogLevel.CRITICAL.isAtLeastAs(LogLevel.ERROR));
        assertTrue(LogLevel.CRITICAL.isAtLeastAs(LogLevel.CRITICAL));
        assertFalse(LogLevel.DEBUG.isAtLeastAs(LogLevel.ERROR));
        assertFalse(LogLevel.INFO.isAtLeastAs(LogLevel.WARNING));
    }
}

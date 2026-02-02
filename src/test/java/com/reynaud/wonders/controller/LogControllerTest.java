package com.reynaud.wonders.controller;

import com.reynaud.wonders.entity.LogEntry;
import com.reynaud.wonders.model.LogLevel;
import com.reynaud.wonders.service.LoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("LogController Tests")
class LogControllerTest {

    @Mock
    private LoggingService loggingService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogController logController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getName()).thenReturn("admin_user");
    }

    @Test
    @DisplayName("Should retrieve all logs with pagination")
    void testGetAllLogs() {
        LogEntry log1 = new LogEntry(LogLevel.INFO, "Message 1", "Source1");
        log1.setId(1L);
        LogEntry log2 = new LogEntry(LogLevel.ERROR, "Message 2", "Source2");
        log2.setId(2L);
        Page<LogEntry> mockPage = new PageImpl<>(Arrays.asList(log1, log2), PageRequest.of(0, 50), 2);

        when(loggingService.getAllLogs(1)).thenReturn(mockPage);

        ResponseEntity<Map<String, Object>> response = logController.getAllLogs(1, null, null, authentication);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("page"));
        assertTrue(response.getBody().containsKey("logs"));
        verify(loggingService, times(1)).getAllLogs(1);
    }

    @Test
    @DisplayName("Should retrieve logs by level")
    void testGetLogsByLevel() {
        LogEntry errorLog = new LogEntry(LogLevel.ERROR, "Error message", "Source");
        errorLog.setId(1L);
        Page<LogEntry> mockPage = new PageImpl<>(Arrays.asList(errorLog), PageRequest.of(0, 50), 1);

        when(loggingService.getLogsByLevel(LogLevel.ERROR, 1)).thenReturn(mockPage);

        ResponseEntity<Map<String, Object>> response = logController.getLogsByLevel(LogLevel.ERROR, 1, null, null, authentication);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("logs"));
        verify(loggingService, times(1)).getLogsByLevel(LogLevel.ERROR, 1);
    }

    @Test
    @DisplayName("Should retrieve logs by source")
    void testGetLogsBySource() {
        LogEntry log = new LogEntry(LogLevel.INFO, "Test", "GameService");
        log.setId(1L);
        Page<LogEntry> mockPage = new PageImpl<>(Arrays.asList(log), PageRequest.of(0, 50), 1);

        when(loggingService.getLogsBySource("GameService", 1)).thenReturn(mockPage);

        ResponseEntity<Map<String, Object>> response = logController.getLogsBySource("GameService", 1, null, null, authentication);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("logs"));
        verify(loggingService, times(1)).getLogsBySource("GameService", 1);
    }

    @Test
    @DisplayName("Should retrieve unread logs")
    void testGetUnreadLogs() {
        LogEntry unreadLog = new LogEntry(LogLevel.WARNING, "Unread", "Source");
        unreadLog.setReadByAdmin(false);

        when(loggingService.getUnreadLogs()).thenReturn(Arrays.asList(unreadLog));

        ResponseEntity<Map<String, Object>> response = logController.getUnreadLogs(null, null, authentication);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("totalUnread"));
        verify(loggingService, times(1)).getUnreadLogs();
    }

    @Test
    @DisplayName("Should retrieve recent logs")
    void testGetRecentLogs() {
        LogEntry recentLog = new LogEntry(LogLevel.INFO, "Recent", "Source");

        when(loggingService.getRecentLogs(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(recentLog));

        ResponseEntity<Map<String, Object>> response = logController.getRecentLogs(30, null, null, authentication);

        assertNotNull(response.getBody());
        assertEquals(30, response.getBody().get("sinceMintuesAgo"));
        verify(loggingService, times(1)).getRecentLogs(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should mark logs as read")
    void testMarkLogsAsRead() {
        List<Long> logIds = Arrays.asList(1L, 2L, 3L);

        ResponseEntity<Map<String, Object>> response = logController.markLogsAsRead(logIds, authentication);

        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().get("logsMarked"));
        verify(loggingService, times(1)).markAsRead(logIds);
    }

    @Test
    @DisplayName("Should mark all logs as read")
    void testMarkAllAsRead() {
        ResponseEntity<Map<String, Object>> response = logController.markAllAsRead(authentication);

        assertNotNull(response.getBody());
        verify(loggingService, times(1)).markAllAsRead();
    }

    @Test
    @DisplayName("Should get unread logs count")
    void testGetUnreadLogsCount() {
        when(loggingService.getUnreadLogsCount()).thenReturn(5L);

        ResponseEntity<Map<String, Object>> response = logController.getUnreadLogsCount(authentication);

        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().get("unreadCount"));
        verify(loggingService, times(1)).getUnreadLogsCount();
    }

    @Test
    @DisplayName("Should clear old logs")
    void testClearOldLogs() {
        when(loggingService.clearOldLogs(30)).thenReturn(10L);

        ResponseEntity<Map<String, Object>> response = logController.clearOldLogs(30, authentication);

        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().get("logsDeleted"));
        verify(loggingService, times(1)).clearOldLogs(30);
    }

    @Test
    @DisplayName("Should health check endpoint")
    void testHealth() {
        ResponseEntity<Map<String, Object>> response = logController.health(authentication);

        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("admin_user", response.getBody().get("admin"));
    }
}

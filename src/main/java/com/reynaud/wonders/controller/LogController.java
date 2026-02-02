package com.reynaud.wonders.controller;

import com.reynaud.wonders.dto.LogEntryDTO;
import com.reynaud.wonders.entity.LogEntry;
import com.reynaud.wonders.model.LogLevel;
import com.reynaud.wonders.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin-only endpoint for viewing and managing application logs in real-time
 * Secured with @PreAuthorize to allow only ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/logs")
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

    private final LoggingService loggingService;

    @Autowired
    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Get all logs with pagination and optional exclusion filters
     * GET /api/admin/logs?page=1&excludeLevels=DEBUG,INFO&excludeSources=MyService
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String excludeLevels,
            @RequestParam(required = false) String excludeSources,
            Authentication authentication) {
        
        Page<LogEntry> logsPage = loggingService.getAllLogs(page);
        
        // Apply exclusion filters on the retrieved page
        if ((excludeLevels != null && !excludeLevels.isEmpty()) || 
            (excludeSources != null && !excludeSources.isEmpty())) {
            logsPage = filterExclusions(logsPage, excludeLevels, excludeSources);
        }
        
        return buildResponse(logsPage, authentication.getName());
    }

    /**
     * Get logs filtered by severity level (with optional exclusions)
     * GET /api/admin/logs/level/ERROR?page=1&excludeLevels=DEBUG&excludeSources=MyService
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getLogsByLevel(
            @PathVariable LogLevel level,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String excludeLevels,
            @RequestParam(required = false) String excludeSources,
            Authentication authentication) {
        
        Page<LogEntry> logsPage = loggingService.getLogsByLevel(level, page);
        
        // Apply exclusion filters on the retrieved page
        if ((excludeLevels != null && !excludeLevels.isEmpty()) || 
            (excludeSources != null && !excludeSources.isEmpty())) {
            logsPage = filterExclusions(logsPage, excludeLevels, excludeSources);
        }
        
        return buildResponse(logsPage, authentication.getName());
    }

    /**
     * Get logs from a specific source (with optional exclusions)
     * GET /api/admin/logs/source/GameService?page=1&excludeLevels=DEBUG&excludeSources=TestService
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<Map<String, Object>> getLogsBySource(
            @PathVariable String source,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String excludeLevels,
            @RequestParam(required = false) String excludeSources,
            Authentication authentication) {
        
        Page<LogEntry> logsPage = loggingService.getLogsBySource(source, page);
        
        // Apply exclusion filters on the retrieved page
        if ((excludeLevels != null && !excludeLevels.isEmpty()) || 
            (excludeSources != null && !excludeSources.isEmpty())) {
            logsPage = filterExclusions(logsPage, excludeLevels, excludeSources);
        }
        
        return buildResponse(logsPage, authentication.getName());
    }

    /**
     * Get unread logs (logs not yet reviewed by admin, with optional exclusions)
     * GET /api/admin/logs/unread?excludeLevels=DEBUG&excludeSources=MyService
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadLogs(
            @RequestParam(required = false) String excludeLevels,
            @RequestParam(required = false) String excludeSources,
            Authentication authentication) {
        List<LogEntry> unreadLogs = loggingService.getUnreadLogs();
        List<LogEntryDTO> dtos = convertToDTO(unreadLogs);
        
        // Apply exclusion filters
        if ((excludeLevels != null && !excludeLevels.isEmpty()) || 
            (excludeSources != null && !excludeSources.isEmpty())) {
            dtos = filterExclusionsFromDTOList(dtos, excludeLevels, excludeSources);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("totalUnread", dtos.size());
        response.put("totalElements", dtos.size());
        response.put("logs", dtos);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent logs (since specified minutes ago, with optional exclusions)
     * GET /api/admin/logs/recent?minutes=30&excludeLevels=DEBUG&excludeSources=MyService
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentLogs(
            @RequestParam(defaultValue = "30") int minutes,
            @RequestParam(required = false) String excludeLevels,
            @RequestParam(required = false) String excludeSources,
            Authentication authentication) {
        
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        List<LogEntry> recentLogs = loggingService.getRecentLogs(since);
        List<LogEntryDTO> dtos = convertToDTO(recentLogs);
        
        // Apply exclusion filters
        if ((excludeLevels != null && !excludeLevels.isEmpty()) || 
            (excludeSources != null && !excludeSources.isEmpty())) {
            dtos = filterExclusionsFromDTOList(dtos, excludeLevels, excludeSources);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("sinceMintuesAgo", minutes);
        response.put("totalElements", dtos.size());
        response.put("logs", dtos);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark specific logs as read by admin
     * POST /api/admin/logs/mark-read
     */
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markLogsAsRead(
            @RequestBody List<Long> logIds,
            Authentication authentication) {
        
        loggingService.markAsRead(logIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("message", "Marked " + logIds.size() + " logs as read");
        response.put("logsMarked", logIds.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all logs as read
     * POST /api/admin/logs/mark-all-read
     */
    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
        long unreadCount = loggingService.getUnreadLogsCount();
        loggingService.markAllAsRead();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("message", "Marked all logs as read");
        response.put("logsMarked", unreadCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get unread logs count (useful for badge/notification)
     * GET /api/admin/logs/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadLogsCount(Authentication authentication) {
        long unreadCount = loggingService.getUnreadLogsCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear old logs (older than X days)
     * DELETE /api/admin/logs/clear-old?days=30
     */
    @DeleteMapping("/clear-old")
    public ResponseEntity<Map<String, Object>> clearOldLogs(
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {
        
        long deletedCount = loggingService.clearOldLogs(days);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("message", "Cleared logs older than " + days + " days");
        response.put("logsDeleted", deletedCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint - accessible only to admins
     * GET /api/admin/logs/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", authentication.getName());
        response.put("message", "Logging service is operational");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to build paginated response
     */
    private ResponseEntity<Map<String, Object>> buildResponse(Page<LogEntry> page, String adminName) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("admin", adminName);
        response.put("page", page.getNumber() + 1);
        response.put("pageSize", page.getSize());
        response.put("totalPages", page.getTotalPages());
        response.put("totalElements", page.getTotalElements());
        response.put("logs", convertToDTO(page.getContent()));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to convert LogEntry to DTO
     */
    private List<LogEntryDTO> convertToDTO(List<LogEntry> logs) {
        return logs.stream().map(log -> {
            LogEntryDTO dto = new LogEntryDTO();
            dto.setId(log.getId());
            dto.setTimestamp(log.getTimestamp());
            dto.setLogLevel(log.getLogLevel());
            dto.setMessage(log.getMessage());
            dto.setSource(log.getSource());
            dto.setStackTrace(log.getStackTrace());
            dto.setReadByAdmin(log.getReadByAdmin());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Helper method to filter Page of LogEntry by excluded levels and sources
     */
    private Page<LogEntry> filterExclusions(Page<LogEntry> page, String excludeLevels, String excludeSources) {
        List<LogEntry> filtered = page.getContent().stream()
                .filter(log -> shouldIncludeLog(log, excludeLevels, excludeSources))
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
                filtered,
                page.getPageable(),
                page.getTotalElements()
        );
    }

    /**
     * Helper method to filter List of LogEntryDTO by excluded levels and sources
     */
    private List<LogEntryDTO> filterExclusionsFromDTOList(List<LogEntryDTO> dtos, String excludeLevels, String excludeSources) {
        return dtos.stream()
                .filter(dto -> shouldIncludeDTO(dto, excludeLevels, excludeSources))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to check if a LogEntry should be included (not excluded)
     */
    private boolean shouldIncludeLog(LogEntry log, String excludeLevels, String excludeSources) {
        // Check if log level should be excluded
        if (excludeLevels != null && !excludeLevels.isEmpty()) {
            String[] levels = excludeLevels.split(",");
            for (String level : levels) {
                if (log.getLogLevel().toString().equalsIgnoreCase(level.trim())) {
                    return false; // Exclude this log
                }
            }
        }
        
        // Check if source should be excluded
        if (excludeSources != null && !excludeSources.isEmpty()) {
            String[] sources = excludeSources.split(",");
            for (String source : sources) {
                if (log.getSource() != null && log.getSource().equalsIgnoreCase(source.trim())) {
                    return false; // Exclude this log
                }
            }
        }
        
        return true; // Include this log
    }

    /**
     * Helper method to check if a LogEntryDTO should be included (not excluded)
     */
    private boolean shouldIncludeDTO(LogEntryDTO dto, String excludeLevels, String excludeSources) {
        // Check if log level should be excluded
        if (excludeLevels != null && !excludeLevels.isEmpty()) {
            String[] levels = excludeLevels.split(",");
            for (String level : levels) {
                if (dto.getLogLevel().toString().equalsIgnoreCase(level.trim())) {
                    return false; // Exclude this log
                }
            }
        }
        
        // Check if source should be excluded
        if (excludeSources != null && !excludeSources.isEmpty()) {
            String[] sources = excludeSources.split(",");
            for (String source : sources) {
                if (dto.getSource() != null && dto.getSource().equalsIgnoreCase(source.trim())) {
                    return false; // Exclude this log
                }
            }
        }
        
        return true; // Include this log
    }
}

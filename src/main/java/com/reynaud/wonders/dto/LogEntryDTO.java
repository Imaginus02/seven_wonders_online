package com.reynaud.wonders.dto;

import com.reynaud.wonders.model.LogLevel;
import java.time.LocalDateTime;

public class LogEntryDTO {
    private Long id;
    private LocalDateTime timestamp;
    private LogLevel logLevel;
    private String message;
    private String source;
    private String stackTrace;
    private Boolean readByAdmin;

    public LogEntryDTO() {
    }

    public LogEntryDTO(Long id, LocalDateTime timestamp, LogLevel logLevel, String message, String source, Boolean readByAdmin) {
        this.id = id;
        this.timestamp = timestamp;
        this.logLevel = logLevel;
        this.message = message;
        this.source = source;
        this.readByAdmin = readByAdmin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Boolean getReadByAdmin() {
        return readByAdmin;
    }

    public void setReadByAdmin(Boolean readByAdmin) {
        this.readByAdmin = readByAdmin;
    }
}

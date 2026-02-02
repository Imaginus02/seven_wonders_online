package com.reynaud.wonders.entity;

import com.reynaud.wonders.model.LogLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs", indexes = {
        @Index(name = "idx_logs_timestamp", columnList = "timestamp"),
        @Index(name = "idx_logs_level", columnList = "log_level"),
        @Index(name = "idx_logs_source", columnList = "source")
})
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false)
    private LogLevel logLevel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(length = 255)
    private String source;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(nullable = false)
    private Boolean readByAdmin = false;

    public LogEntry() {
    }

    public LogEntry(LogLevel logLevel, String message, String source) {
        this.timestamp = LocalDateTime.now();
        this.logLevel = logLevel;
        this.message = message;
        this.source = source;
    }

    public LogEntry(LogLevel logLevel, String message, String source, String stackTrace) {
        this(logLevel, message, source);
        this.stackTrace = stackTrace;
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

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", logLevel=" + logLevel +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}

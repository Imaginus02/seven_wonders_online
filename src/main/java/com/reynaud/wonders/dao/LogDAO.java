package com.reynaud.wonders.dao;

import com.reynaud.wonders.entity.LogEntry;
import com.reynaud.wonders.model.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogDAO extends JpaRepository<LogEntry, Long> {

    /**
     * Find all logs ordered by timestamp (most recent first)
     */
    Page<LogEntry> findAllByOrderByTimestampDesc(Pageable pageable);

    /**
     * Find logs by level and timestamp range
     */
    List<LogEntry> findByLogLevelAndTimestampBetweenOrderByTimestampDesc(
            LogLevel logLevel, LocalDateTime start, LocalDateTime end);

    /**
     * Find logs by source
     */
    Page<LogEntry> findBySourceOrderByTimestampDesc(String source, Pageable pageable);

    /**
     * Find logs with minimum severity level
     */
    @Query("SELECT l FROM LogEntry l WHERE l.logLevel IN :levels ORDER BY l.timestamp DESC")
    Page<LogEntry> findByLogLevelIn(@Param("levels") List<LogLevel> levels, Pageable pageable);

    /**
     * Find unread logs by admin
     */
    List<LogEntry> findByReadByAdminFalseOrderByTimestampDesc();

    /**
     * Find logs since a specific time
     */
    List<LogEntry> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);

    /**
     * Find logs before a specific time
     */
    List<LogEntry> findByTimestampBeforeOrderByTimestampDesc(LocalDateTime timestamp);

    /**
     * Count unread logs
     */
    long countByReadByAdminFalse();
}

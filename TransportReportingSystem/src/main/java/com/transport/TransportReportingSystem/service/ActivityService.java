package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.entity.ActivityLog;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(String title, String description, String type, User user) {
        ActivityLog log = ActivityLog.builder()
                .title(title)
                .description(description)
                .type(type)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();
        activityLogRepository.save(log);
    }

    @Transactional
    public void logSuccess(String title, String description, User user) {
        logActivity(title, description, "success", user);
    }

    @Transactional
    public void logInfo(String title, String description, User user) {
        logActivity(title, description, "info", user);
    }

    @Transactional
    public void logWarning(String title, String description, User user) {
        logActivity(title, description, "warning", user);
    }

    @Transactional
    public void logError(String title, String description, User user) {
        logActivity(title, description, "error", user);
    }
}

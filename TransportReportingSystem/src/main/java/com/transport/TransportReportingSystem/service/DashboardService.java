package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.DashboardSummaryDTO;
import com.transport.TransportReportingSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RouteRepository routeRepository;
    private final FeedbackRepository feedbackRepository;
    private final LocationRepository locationRepository;
    private final ActivityLogRepository activityLogRepository;

    public DashboardSummaryDTO getSummary(java.security.Principal principal) {
        // Fetch current user
        com.transport.TransportReportingSystem.entity.User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardSummaryDTO.DashboardSummaryDTOBuilder builder = DashboardSummaryDTO.builder();
        java.util.List<com.transport.TransportReportingSystem.entity.ActivityLog> logs;

        if ("COMPANY_ADMIN".equals(user.getRole().name())) {
            // Filter for Company Admin
            Long companyId = (user.getCompany() != null) ? user.getCompany().getCompanyId() : null;
            
            if (companyId == null) {
                return DashboardSummaryDTO.builder()
                        .recentActivities(new java.util.ArrayList<>())
                        .build();
            }

            builder.totalUsers(userRepository.countByCompanyCompanyId(companyId))
                    .totalCompanies(1)
                    .totalRoutes(routeRepository.countByCompanyCompanyId(companyId))
                    .totalFeedbacks(feedbackRepository.countByRouteCompanyCompanyId(companyId))
                    .pendingFeedbacks(feedbackRepository.countByRouteCompanyCompanyIdAndStatus(companyId, com.transport.TransportReportingSystem.enums.FeedbackStatus.PENDING))
                    .resolvedFeedbacks(feedbackRepository.countByRouteCompanyCompanyIdAndStatus(companyId, com.transport.TransportReportingSystem.enums.FeedbackStatus.RESOLVED))
                    .totalLocations(locationRepository.count());

            logs = activityLogRepository.findTop10ByUserCompanyCompanyIdOrderByTimestampDesc(companyId);
        } else if ("SUPER_ADMIN".equals(user.getRole().name())) {
            // SUPER_ADMIN (Global Stats)
            builder.totalUsers(userRepository.count())
                    .totalCompanies(companyRepository.count())
                    .totalRoutes(routeRepository.count())
                    .totalFeedbacks(feedbackRepository.count())
                    .pendingFeedbacks(feedbackRepository.countByStatus(com.transport.TransportReportingSystem.enums.FeedbackStatus.PENDING))
                    .resolvedFeedbacks(feedbackRepository.countByStatus(com.transport.TransportReportingSystem.enums.FeedbackStatus.RESOLVED))
                    .totalLocations(locationRepository.count());

            logs = activityLogRepository.findTop10ByOrderByTimestampDesc();
        } else {
            // Regular USER: Only their own data
            builder.totalFeedbacks(feedbackRepository.countByUser(user))
                    .pendingFeedbacks(feedbackRepository.countByUserAndStatus(user, com.transport.TransportReportingSystem.enums.FeedbackStatus.PENDING))
                    .resolvedFeedbacks(feedbackRepository.countByUserAndStatus(user, com.transport.TransportReportingSystem.enums.FeedbackStatus.RESOLVED));
            
            logs = activityLogRepository.findTop10ByUserOrderByTimestampDesc(user);
        }

        builder.recentActivities(logs.stream()
                .map(this::mapToActivityDTO)
                .collect(java.util.stream.Collectors.toList()));

        return builder.build();
    }

    private com.transport.TransportReportingSystem.dto.ActivityLogDTO mapToActivityDTO(com.transport.TransportReportingSystem.entity.ActivityLog log) {
        return com.transport.TransportReportingSystem.dto.ActivityLogDTO.builder()
                .logId(log.getLogId())
                .title(log.getTitle())
                .description(log.getDescription())
                .type(log.getType())
                .timestamp(log.getTimestamp())
                .userName(log.getUser() != null ? log.getUser().getName() : "System")
                .timeAgo(formatTimeAgo(log.getTimestamp()))
                .build();
    }

    private String formatTimeAgo(java.time.LocalDateTime timestamp) {
        if (timestamp == null) return "Unknown";
        java.time.Duration duration = java.time.Duration.between(timestamp, java.time.LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Just now";
        if (seconds < 3600) return (seconds / 60) + "m ago";
        if (seconds < 86400) return (seconds / 3600) + "h ago";
        return (seconds / 86400) + "d ago";
    }
}

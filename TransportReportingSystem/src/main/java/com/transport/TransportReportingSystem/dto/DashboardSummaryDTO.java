package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {
    private long totalUsers;
    private long totalCompanies;
    private long totalRoutes;
    private long totalFeedbacks;
    private long pendingFeedbacks;
    private long resolvedFeedbacks;
    private long totalLocations;
    private java.util.List<ActivityLogDTO> recentActivities;
}

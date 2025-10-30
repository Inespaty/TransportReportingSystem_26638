package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private Long feedbackId;
    private String title;
    private String description;
    private String imageUrl;
    private String status;
    private String issueCategory;
    private Long userId;
    private String userName;
    private Long routeId;
    private String routeName;
    private Long assignedAdminId;
    private String assignedAdminName;
    private Long incidentLocationId;
    private String incidentLocationName;
}

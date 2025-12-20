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
    private String adminResponse;
    private String status;
    private String issueCategory;
    private Long userId;
    private String userName;
    private Long routeId;
    private String routeName;
    private Long assignedUserId;
    private String assignedUserName;
    private Long incidentLocationId;
    private String incidentLocationName;
    private Long companyId;
    private String companyName;
    private String createdAt;
}

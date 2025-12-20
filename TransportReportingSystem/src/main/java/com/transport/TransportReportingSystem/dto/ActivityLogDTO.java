package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDTO {
    private Long logId;
    private String title;
    private String description;
    private String type;
    private LocalDateTime timestamp;
    private String userName;
    private String timeAgo; // Formatted string like "5 minutes ago"
}

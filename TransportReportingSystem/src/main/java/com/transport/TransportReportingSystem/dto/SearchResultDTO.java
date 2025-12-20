package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private String type; // "User", "Company", "Route", "Location"
    private Long id;
    private String title; // Name, Route Number, etc.
    private String description; // Email, Start-End, etc.
    private String link; // Frontend route to view details
}

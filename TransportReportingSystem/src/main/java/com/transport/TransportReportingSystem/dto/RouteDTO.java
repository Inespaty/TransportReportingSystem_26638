package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private String startPoint;
    private String endPoint;
    private String direction;
    private String district;
    private Long companyId;
    private String companyName;
}

package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long locationId;
    private String locationName;
    private String locationType;
    private Long parentLocationId;
    private String parentLocationName;
}

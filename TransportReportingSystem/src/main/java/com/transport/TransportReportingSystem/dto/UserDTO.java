package com.transport.TransportReportingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private LocalDate createdAt;
    private Long locationId;
    private String locationName;
    private Long companyId;
    private String companyName;
}
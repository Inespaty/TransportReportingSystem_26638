package com.transport.TransportReportingSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;
    
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;
    
    @Column(name = "description", length = 100)
    private String description;
    
    @Column(name = "contact_info", length = 200)
    private String contactInfo;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Route> routes;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Admin> admins;
}

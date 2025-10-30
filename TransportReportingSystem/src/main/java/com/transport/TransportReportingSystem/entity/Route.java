package com.transport.TransportReportingSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "route")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;
    
    @Column(name = "route_number", unique = true, nullable = false, length = 10)
    private String routeNumber;
    
    @Column(name = "route_name", length = 150)
    private String routeName;
    
    @Column(name = "start_point", length = 100)
    private String startPoint;
    
    @Column(name = "end_point", length = 100)
    private String endPoint;
    
    @Column(name = "direction", length = 20)
    private String direction;
    
    @Column(name = "district", length = 50)
    private String district;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @ManyToMany
    @JoinTable(
        name = "user_route",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}

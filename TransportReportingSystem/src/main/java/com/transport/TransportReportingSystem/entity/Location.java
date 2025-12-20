package com.transport.TransportReportingSystem.entity;

import com.transport.TransportReportingSystem.enums.LocationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;
    
    @Column(name = "location_name", nullable = false)
    private String locationName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")

    private Location parentLocation;
    
    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL)
   
    private List<Location> childLocations;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    
    private List<User> users;
}

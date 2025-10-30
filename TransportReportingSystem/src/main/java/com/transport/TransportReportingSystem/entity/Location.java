package com.transport.TransportReportingSystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonBackReference
    private Location parentLocation;
    
    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Location> childLocations;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<User> users;
}

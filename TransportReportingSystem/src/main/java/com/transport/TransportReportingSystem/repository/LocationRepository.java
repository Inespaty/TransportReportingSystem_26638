package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.enums.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    
    Optional<Location> findByLocationName(String locationName);
    
    Optional<Location> findByLocationNameAndParentLocation(String locationName, Location parentLocation);
    
    
    List<Location> findByLocationType(LocationType locationType);
    
    
    List<Location> findByLocationNameContaining(String keyword);
    
    
    List<Location> findByParentLocation(Location parentLocation);

    List<Location> findByParentLocationIsNull();
    
    
    List<Location> findByParentLocationLocationId(Long parentId);
    
    
    List<Location> findByLocationTypeAndLocationNameContaining(LocationType type, String keyword);

    
}

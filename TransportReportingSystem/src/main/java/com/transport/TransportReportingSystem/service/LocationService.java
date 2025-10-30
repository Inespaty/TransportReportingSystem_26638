package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.LocationDTO;
import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.enums.LocationType;
import com.transport.TransportReportingSystem.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = new Location();
        location.setLocationName(locationDTO.getLocationName());
        location.setLocationType(LocationType.valueOf(locationDTO.getLocationType()));
        
        
        if (locationDTO.getParentLocationId() != null) {
            Location parent = locationRepository.findById(locationDTO.getParentLocationId())
                .orElseThrow(() -> new RuntimeException("Parent location not found"));
            location.setParentLocation(parent);
        }
        
        Location savedLocation = locationRepository.save(location);
        return convertToDTO(savedLocation);
    }
    
    
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found"));
        return convertToDTO(location);
    }
    
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public List<LocationDTO> getAllProvinces() {
        return locationRepository.findByParentLocationIsNull().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public List<LocationDTO> getDistrictsByProvince(String provinceName) {
        Location province = locationRepository.findByLocationName(provinceName)
            .orElseThrow(() -> new RuntimeException("Province not found"));
        
        return locationRepository.findByParentLocation(province).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public List<LocationDTO> getLocationsByType(String locationType) {
        return locationRepository.findByLocationType(LocationType.valueOf(locationType)).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found"));
        
        location.setLocationName(locationDTO.getLocationName());
        location.setLocationType(LocationType.valueOf(locationDTO.getLocationType()));
       
        
        if (locationDTO.getParentLocationId() != null) {
            Location parent = locationRepository.findById(locationDTO.getParentLocationId())
                .orElseThrow(() -> new RuntimeException("Parent location not found"));
            location.setParentLocation(parent);
        }
        
        Location updatedLocation = locationRepository.save(location);
        return convertToDTO(updatedLocation);
    }
    
    
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found");
        }
        locationRepository.deleteById(id);
    }
    

  

    
   
    private LocationDTO convertToDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setLocationId(location.getLocationId());
        dto.setLocationName(location.getLocationName());
        dto.setLocationType(location.getLocationType().toString());
        if (location.getParentLocation() != null) {
            dto.setParentLocationId(location.getParentLocation().getLocationId());
            dto.setParentLocationName(location.getParentLocation().getLocationName());
        }
        
        return dto;
    }
}

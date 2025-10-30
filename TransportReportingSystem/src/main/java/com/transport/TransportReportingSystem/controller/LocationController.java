package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.LocationDTO;
import com.transport.TransportReportingSystem.service.LocationService;
import com.transport.TransportReportingSystem.service.UserService;
import com.transport.TransportReportingSystem.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService locationService;
    private final UserService userService;
    
    
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(
            @RequestParam String locationName,
            @RequestParam String locationType,
            @RequestParam Long parentLocationId) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocationName(locationName);
        locationDTO.setLocationType(locationType);
        
        locationDTO.setParentLocationId(parentLocationId);
        LocationDTO createdLocation = locationService.createLocation(locationDTO);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    
    @GetMapping("/provinces")
    public ResponseEntity<List<LocationDTO>> getAllProvinces() {
        List<LocationDTO> provinces = locationService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        LocationDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }
    
   
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }
    
    
    
    
    @GetMapping("/{provinceName}/districts")
    public ResponseEntity<List<LocationDTO>> getDistrictsByProvince(@PathVariable String provinceName) {
        List<LocationDTO> districts = locationService.getDistrictsByProvince(provinceName);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/{provinceId}/users")
    public ResponseEntity<List<UserDTO>> getUsersByProvince(@PathVariable Long provinceId) {
        try {
            List<UserDTO> users = userService.getUsersByProvince(provinceId);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
           
            return ResponseEntity.notFound().build();
        }
    }
    
   
    @GetMapping("/type/{locationType}")
    public ResponseEntity<List<LocationDTO>> getLocationsByType(@PathVariable String locationType) {
        List<LocationDTO> locations = locationService.getLocationsByType(locationType);
        return ResponseEntity.ok(locations);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id,
            @RequestParam String locationName,
            @RequestParam String locationType,
            
            @RequestParam Long parentLocationId) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setLocationName(locationName);
        locationDTO.setLocationType(locationType);
        locationDTO.setParentLocationId(parentLocationId);
        LocationDTO updatedLocation = locationService.updateLocation(id, locationDTO);
        return ResponseEntity.ok(updatedLocation);
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
 

}
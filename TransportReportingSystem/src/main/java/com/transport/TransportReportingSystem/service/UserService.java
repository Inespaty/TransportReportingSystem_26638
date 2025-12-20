package com.transport.TransportReportingSystem.service;


import com.transport.TransportReportingSystem.dto.UserDTO;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Location;

import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.enums.UserRole;
import com.transport.TransportReportingSystem.repository.UserRepository;

import com.transport.TransportReportingSystem.repository.LocationRepository;
import com.transport.TransportReportingSystem.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.transport.TransportReportingSystem.enums.LocationType;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CompanyRepository companyRepository;
    private final ActivityService activityService;

    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
   
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("User with email " + userDTO.getEmail() + " already exists");
        }
        
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        
        user.setRole(UserRole.valueOf(userDTO.getRole()));
        user.setCreatedAt(LocalDate.now());
        
        Long locationId = userDTO.getLocationId();
        
        // Enforce location for standard Users
        if (user.getRole() == UserRole.USER && locationId == null) {
            throw new RuntimeException("Location is required for standard users.");
        }
        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
            user.setLocation(location);
        }
        
        Long companyId = userDTO.getCompanyId();
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
            user.setCompany(company);
        }
        
        user.setIsTwoFactorEnabled(userDTO.getIsTwoFactorEnabled() != null ? userDTO.getIsTwoFactorEnabled() : false);
        
        

        
        User savedUser = userRepository.save(user);
        activityService.logSuccess("New User Created", "Account for " + savedUser.getName() + " was successfully created.", null);
        return convertToDTO(savedUser);
    }
    
    
    public UserDTO getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public Page<UserDTO> getAllUsersPaginated(String search, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        Page<User> usersPage;
        if (search != null && !search.trim().isEmpty()) {
            usersPage = userRepository.searchUsers(search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        
        return usersPage.map(this::convertToDTO);
    }
    
   
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            user.setPassword(encryptedPassword);
        }
        
    UserRole newRole = UserRole.valueOf(userDTO.getRole());
    user.setRole(newRole);
        
        Long locationId = userDTO.getLocationId();
        
        // Enforce location for standard Users
        if (newRole == UserRole.USER && locationId == null) {
            throw new RuntimeException("Location is required for standard users.");
        }
        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
            user.setLocation(location);
        }
        
        Long companyId = userDTO.getCompanyId();
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
            user.setCompany(company);
        }

        if (userDTO.getIsTwoFactorEnabled() != null) {
            user.setIsTwoFactorEnabled(userDTO.getIsTwoFactorEnabled());
        }

        
        // Admin role logic removed as Admin entity is deprecated. Use UserRole.

        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User userToDelete = userRepository.findById(id).get();
        String userName = userToDelete.getName();
        userRepository.deleteById(id);
        activityService.logWarning("User Account Deleted", "The account for " + userName + " was removed from the system.", null);
    }
    
    
    public List<UserDTO> getUsersByLocation(String locationName) {
        List<User> users = userRepository.findByLocationLocationNameContaining(locationName);
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserDTO> getUsersByProvince(String provinceName) {
       
        Location province = locationRepository.findByLocationName(provinceName)
            .orElseThrow(() -> new RuntimeException("Province not found"));

        if (province.getLocationType() != LocationType.PROVINCE) {
            throw new RuntimeException("Province not found");
        }

        List<Location> locations = new ArrayList<>();
        collectLocationsRecursively(province, locations);

        List<User> users = userRepository.findByLocationIn(locations);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
}


  
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
  
    public boolean validateLogin(String email, String password) {
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
           
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
    
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword("***"); 
        dto.setRole(user.getRole() != null ? user.getRole().toString() : "UNKNOWN");
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getLocation() != null) {
            dto.setLocationId(user.getLocation().getLocationId());
            dto.setLocationName(user.getLocation().getLocationName());
            
            // Populate hierarchy
            Location loc = user.getLocation();
            while (loc != null) {
                switch (loc.getLocationType()) {
                    case PROVINCE:
                        dto.setProvinceName(loc.getLocationName());
                        break;
                    case DISTRICT:
                        dto.setDistrictName(loc.getLocationName());
                        break;
                    case SECTOR:
                        dto.setSectorName(loc.getLocationName());
                        break;
                    case CELL:
                        dto.setCellName(loc.getLocationName());
                        break;
                    case VILLAGE:
                        dto.setVillageName(loc.getLocationName());
                        break;
                    default:
                        // Handle unknown location type
                        break;
                }
                loc = loc.getParentLocation();
            }
        }
        
        // UserProfile removed from system
        
        if (user.getCompany() != null) {
            dto.setCompanyId(user.getCompany().getCompanyId());
            dto.setCompanyName(user.getCompany().getCompanyName());
        }
        
        dto.setIsTwoFactorEnabled(user.getIsTwoFactorEnabled());
        
        return dto;
    }

    public List<UserDTO> getUsersByProvince(Long provinceId) {
        if (provinceId == null) {
            throw new IllegalArgumentException("Province ID cannot be null");
        }
        Location province = locationRepository.findById(provinceId)
            .orElseThrow(() -> new RuntimeException("Province not found"));

        if (province.getLocationType() != LocationType.PROVINCE) {
            throw new RuntimeException("Province not found");
        }

        List<Location> locations = new ArrayList<>();
        collectLocationsRecursively(province, locations);

        List<User> users = userRepository.findByLocationIn(locations);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private void collectLocationsRecursively(Location loc, List<Location> acc) {
        acc.add(loc);
        if (loc.getChildLocations() != null) {
            for (Location child : loc.getChildLocations()) {
                collectLocationsRecursively(child, acc);
            }
        }
    }
}

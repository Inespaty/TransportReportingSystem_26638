package com.transport.TransportReportingSystem.service;


import com.transport.TransportReportingSystem.dto.UserDTO;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.Admin;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.enums.UserRole;
import com.transport.TransportReportingSystem.repository.UserRepository;
import com.transport.TransportReportingSystem.repository.AdminRepository;
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
    private final AdminRepository adminRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
   
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        
        user.setRole(UserRole.valueOf(userDTO.getRole()));
        user.setCreatedAt(LocalDate.now());
        
        if (userDTO.getLocationId() != null) {
            Location location = locationRepository.findById(userDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
            user.setLocation(location);
        }
        
        if (userDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(userDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            user.setCompany(company);
        }
        
        
        if (user.getRole() == UserRole.COMPANY_ADMIN || user.getRole() == UserRole.SUPER_ADMIN) {
            Admin admin = new Admin();
            admin.setUser(user);
            admin.setCompany(user.getCompany());
            admin.setIsSuperAdmin(user.getRole() == UserRole.SUPER_ADMIN);
            user.setAdmin(admin); 
        }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public Page<UserDTO> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
   
    public UserDTO updateUser(Long id, UserDTO userDTO) {
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
        
        if (userDTO.getLocationId() != null) {
            Location location = locationRepository.findById(userDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
            user.setLocation(location);
        }
        
        if (userDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(userDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            user.setCompany(company);
        }

        
        if (newRole == UserRole.COMPANY_ADMIN || newRole == UserRole.SUPER_ADMIN) {
            if (user.getAdmin() == null) {
                Admin admin = new Admin();
                admin.setUser(user);
                admin.setCompany(user.getCompany());
                admin.setIsSuperAdmin(newRole == UserRole.SUPER_ADMIN);
                user.setAdmin(admin);
            } else {
               
                user.getAdmin().setCompany(user.getCompany());
                user.getAdmin().setIsSuperAdmin(newRole == UserRole.SUPER_ADMIN);
            }
        } else {
            
            if (user.getAdmin() != null) {
                
                Admin existing = user.getAdmin();
                user.setAdmin(null);
                try {
                    adminRepository.delete(existing);
                } catch (Exception ignored) {
                   
                }
            }
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    
    public List<UserDTO> getUsersByLocation(String locationName) {
        List<User> users = userRepository.findByLocationLocationNameContaining(locationName);
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserDTO> getUsersByProvince(String provinceName) {
        // Find province by name, ensure it's a PROVINCE, then collect all descendant locations
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
        dto.setRole(user.getRole().toString());
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getLocation() != null) {
            dto.setLocationId(user.getLocation().getLocationId());
            dto.setLocationName(user.getLocation().getLocationName());
        }
        
        if (user.getCompany() != null) {
            dto.setCompanyId(user.getCompany().getCompanyId());
            dto.setCompanyName(user.getCompany().getCompanyName());
        }
        
        return dto;
    }

    public List<UserDTO> getUsersByProvince(Long provinceId) {
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

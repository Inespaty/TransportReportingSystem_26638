package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.UserDTO;
import com.transport.TransportReportingSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam Long locationId,
            @RequestParam(required = false) Long companyId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(name);
        userDTO.setEmail(email);
        userDTO.setPhone(phone);
        userDTO.setPassword(password);
        userDTO.setRole(role);
        userDTO.setLocationId(locationId);
        userDTO.setCompanyId(companyId);
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
   
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
  
    @GetMapping("/paginated")
    public ResponseEntity<Page<UserDTO>> getAllUsersPaginated(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }
    
   
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam Long locationId,
            @RequestParam(required = false) Long companyId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(name);
        userDTO.setEmail(email);
        userDTO.setPhone(phone);
        userDTO.setPassword(password);
        userDTO.setRole(role);
        userDTO.setLocationId(locationId);
        userDTO.setCompanyId(companyId);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/by-location/{locationName}")
    public ResponseEntity<List<UserDTO>> getUsersByLocation(@PathVariable String locationName) {
        List<UserDTO> users = userService.getUsersByLocation(locationName);
        return ResponseEntity.ok(users);
    }
    
   @GetMapping("/by-province/{provinceName}")
public ResponseEntity<List<UserDTO>> getUsersByProvince(@PathVariable String provinceName) {
    List<UserDTO> users = userService.getUsersByProvince(provinceName);
    return ResponseEntity.ok(users);
}


    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password) {
        boolean isValid = userService.validateLogin(email, password);
        if (isValid) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}

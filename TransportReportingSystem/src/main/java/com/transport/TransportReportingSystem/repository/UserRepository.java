package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    
    Optional<User> findByEmail(String email);
    
    
    boolean existsByEmail(String email);
    
    
    List<User> findByRole(String role);
    
    
    List<User> findByLocation(Location location);
    
    
    List<User> findByLocationLocationNameContaining(String keyword);
   
    
    List<User> findByCompanyCompanyName(String companyName);
    
    List<User> findByLocationIn(List<Location> locations);
    
    // Pagination 
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);
    
    @NonNull
    Page<User> findByRole(@NonNull String role, @NonNull Pageable pageable);
}

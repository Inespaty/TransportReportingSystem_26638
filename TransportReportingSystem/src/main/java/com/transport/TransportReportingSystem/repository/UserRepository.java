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
    
    
    List<User> findByRole(com.transport.TransportReportingSystem.enums.UserRole role);
    
    
    List<User> findByLocation(Location location);
    
    
    List<User> findByLocationLocationNameContaining(String keyword);
   
    
    List<User> findByCompanyCompanyName(String companyName);
    
    List<User> findByLocationIn(List<Location> locations);
    
    // Pagination 
    @NonNull
    Page<User> findByRole(@NonNull com.transport.TransportReportingSystem.enums.UserRole role, @NonNull Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.company.companyId = :companyId AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsersByCompany(@org.springframework.data.repository.query.Param("companyId") Long companyId, @org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    long countByCompanyCompanyId(Long companyId);
}
